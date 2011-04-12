package org.hsqldb.test;

// nbazin@users - enhancements to the original code
// fredt@users - 20050202 - corrected getRandomID(int) to return a randomly distributed value
/*
 *  This is a sample implementation of the Transaction Processing Performance
 *  Council Benchmark B coded in Java and ANSI SQL2.
 *
 *  This version is using one connection per thread to parallellize
 *  server operations.
 * @author Mark Matthews (mark@mysql.com)
 */
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

class JDBCBench {

    /* tpc bm b scaling rules */
    public static int tps       = 1;         /* the tps scaling factor: here it is 1 */
    public static int nbranches = 1;         /* number of branches in 1 tps db       */
    public static int ntellers  = 10;        /* number of tellers in  1 tps db       */
    public static int naccounts = 100000;    /* number of accounts in 1 tps db       */
    public static int nhistory = 864000;     /* number of history recs in 1 tps db   */
    public static final int TELLER              = 0;
    public static final int BRANCH              = 1;
    public static final int ACCOUNT             = 2;
    int                     failed_transactions = 0;
    int                     transaction_count   = 0;
    static int              n_clients           = 10;
    static int              n_txn_per_client    = 10;
    long                    start_time          = 0;
    static boolean          transactions        = true;
    static boolean          prepared_stmt       = false;
    static String           tableExtension      = "";
    static String           createExtension     = "";
    static String           ShutdownCommand     = "";
    static String           startupCommand      = "";
    static PrintStream      TabFile             = null;
    static boolean          verbose             = false;
    MemoryWatcherThread     MemoryWatcher;

    /* main program,    creates a 1-tps database:  i.e. 1 branch, 10 tellers,...
     *                    runs one TPC BM B transaction
     * example command line:
     * -driver  org.hsqldb.jdbcDriver -url jdbc:hsqldb:/hsql/jdbcbench/test -user sa -clients 20 -tpc 10000
     */
    public static void main(String[] Args) {

        String  DriverName         = "";
        String  DBUrl              = "";
        String  DBUser             = "";
        String  DBPassword         = "";
        boolean initialize_dataset = false;

        for (int i = 0; i < Args.length; i++) {
            if (Args[i].equals("-clients")) {
                if (i + 1 < Args.length) {
                    i++;

                    n_clients = Integer.parseInt(Args[i]);
                }
            } else if (Args[i].equals("-driver")) {
                if (i + 1 < Args.length) {
                    i++;

                    DriverName = Args[i];

                    if (DriverName.equals(
                            "org.enhydra.instantdb.jdbc.idbDriver")) {
                        ShutdownCommand = "SHUTDOWN";
                    }

                    if (DriverName.equals(
                            "com.borland.datastore.jdbc.DataStoreDriver")) {}

                    if (DriverName.equals("com.mckoi.JDBCDriver")) {
                        ShutdownCommand = "SHUTDOWN";
                    }

                    if (DriverName.equals("org.hsqldb.jdbcDriver")) {
                        tableExtension  = "CREATE CACHED TABLE ";
                        ShutdownCommand = "SHUTDOWN";
                        startupCommand  = "";
                    }
                }
            } else if (Args[i].equals("-url")) {
                if (i + 1 < Args.length) {
                    i++;

                    DBUrl = Args[i];
                }
            } else if (Args[i].equals("-user")) {
                if (i + 1 < Args.length) {
                    i++;

                    DBUser = Args[i];
                }
            } else if (Args[i].equals("-tabfile")) {
                if (i + 1 < Args.length) {
                    i++;

                    try {
                        FileOutputStream File = new FileOutputStream(Args[i]);

                        TabFile = new PrintStream(File);
                    } catch (Exception e) {
                        TabFile = null;
                    }
                }
            } else if (Args[i].equals("-password")) {
                if (i + 1 < Args.length) {
                    i++;

                    DBPassword = Args[i];
                }
            } else if (Args[i].equals("-tpc")) {
                if (i + 1 < Args.length) {
                    i++;

                    n_txn_per_client = Integer.parseInt(Args[i]);
                }
            } else if (Args[i].equals("-init")) {
                initialize_dataset = true;
            } else if (Args[i].equals("-tps")) {
                if (i + 1 < Args.length) {
                    i++;

                    tps = Integer.parseInt(Args[i]);
                }
            } else if (Args[i].equals("-v")) {
                verbose = true;
            }
        }

        if (DriverName.length() == 0 || DBUrl.length() == 0) {
            System.out.println(
                "usage: java JDBCBench -driver [driver_class_name] -url [url_to_db] -user [username] -password [password] [-v] [-init] [-tpc n] [-clients n]");
            System.out.println();
            System.out.println("-v          verbose error messages");
            System.out.println("-init       initialize the tables");
            System.out.println("-tpc        transactions per client");
            System.out.println("-clients    number of simultaneous clients");
            System.exit(-1);
        }

        System.out.println(
            "*********************************************************");
        System.out.println(
            "* JDBCBench v1.1                                        *");
        System.out.println(
            "*********************************************************");
        System.out.println();
        System.out.println("Driver: " + DriverName);
        System.out.println("URL:" + DBUrl);
        System.out.println();
        System.out.println("Scale factor value: " + tps);
        System.out.println("Number of clients: " + n_clients);
        System.out.println("Number of transactions per client: "
                           + n_txn_per_client);
        System.out.println();

        try {
            Class.forName(DriverName);

            JDBCBench Me = new JDBCBench(DBUrl, DBUser, DBPassword,
                                         initialize_dataset);
        } catch (Exception E) {
            System.out.println(E.getMessage());
            E.printStackTrace();
        }
    }

    public JDBCBench(String url, String user, String password, boolean init) {

        Vector      vClient  = new Vector();
        Thread      Client   = null;
        Enumeration e        = null;
        Connection  guardian = null;

        try {
            java.util.Date start = new java.util.Date();

            if (init) {
                System.out.println("Start: " + start.toString());
                System.out.print("Initializing dataset...");
                createDatabase(url, user, password);

                double seconds = (System.currentTimeMillis() - start.getTime())
                                 / 1000D;

                System.out.println("done. in " + seconds + " seconds\n");
                System.out.println("Complete: "
                                   + (new java.util.Date()).toString());
            }

            guardian = connect(url, user, password);

            if (startupCommand.length() != 0) {
                Statement statement = guardian.createStatement();

                statement.execute(startupCommand);
                statement.close();
            }

            System.out.println("* Starting Benchmark Run *");

            MemoryWatcher = new MemoryWatcherThread();

            MemoryWatcher.start();

            transactions  = true;
            prepared_stmt = false;
            start_time    = System.currentTimeMillis();

            for (int i = 0; i < n_clients; i++) {
                Client = new ClientThread(n_txn_per_client, url, user,
                                          password);

                Client.start();
                vClient.addElement(Client);
            }

            /*
             ** Barrier to complete this test session
             */
            e = vClient.elements();

            while (e.hasMoreElements()) {
                Client = (Thread) e.nextElement();

                Client.join();
            }

            vClient.removeAllElements();
            reportDone();
            checkSums(guardian);

            // debug - allows stopping the test
            if (!transactions) {
                throw new Exception("end after one round");
            }

            transactions  = true;
            prepared_stmt = false;
            start_time    = System.currentTimeMillis();

            for (int i = 0; i < n_clients; i++) {
                Client = new ClientThread(n_txn_per_client, url, user,
                                          password);

                Client.start();
                vClient.addElement(Client);
            }

            /*
             ** Barrier to complete this test session
             */
            e = vClient.elements();

            while (e.hasMoreElements()) {
                Client = (Thread) e.nextElement();

                Client.join();
            }

            vClient.removeAllElements();
            reportDone();
            checkSums(guardian);

            transactions  = true;
            prepared_stmt = true;
            start_time    = System.currentTimeMillis();

            for (int i = 0; i < n_clients; i++) {
                Client = new ClientThread(n_txn_per_client, url, user,
                                          password);

                Client.start();
                vClient.addElement(Client);
            }

            /*
             ** Barrier to complete this test session
             */
            e = vClient.elements();

            while (e.hasMoreElements()) {
                Client = (Thread) e.nextElement();

                Client.join();
            }

            vClient.removeAllElements();
            reportDone();
            checkSums(guardian);

            transactions  = true;
            prepared_stmt = true;
            start_time    = System.currentTimeMillis();

            for (int i = 0; i < n_clients; i++) {
                Client = new ClientThread(n_txn_per_client, url, user,
                                          password);

                Client.start();
                vClient.addElement(Client);
            }

            /*
             ** Barrier to complete this test session
             */
            e = vClient.elements();

            while (e.hasMoreElements()) {
                Client = (Thread) e.nextElement();

                Client.join();
            }

            vClient.removeAllElements();
            reportDone();
            checkSums(guardian);
        } catch (Exception E) {
            System.out.println(E.getMessage());
            E.printStackTrace();
        } finally {
            MemoryWatcher.end();

            try {
                MemoryWatcher.join();

                if (ShutdownCommand.length() > 0) {
                    Statement Stmt = guardian.createStatement();

                    Stmt.execute(ShutdownCommand);
                    Stmt.close();
                    connectClose(guardian);
                }

                if (TabFile != null) {
                    TabFile.close();
                }
            } catch (Exception E1) {}

//            System.exit(0);
        }
    }

    public void reportDone() {

        long end_time = System.currentTimeMillis();
        double completion_time = ((double) end_time - (double) start_time)
                                 / 1000;

        if (TabFile != null) {
            TabFile.print(tps + ";" + n_clients + ";" + n_txn_per_client
                          + ";");
        }

        System.out.println("\n* Benchmark Report *");
        System.out.print("* Featuring ");

        if (prepared_stmt) {
            System.out.print("<prepared statements> ");

            if (TabFile != null) {
                TabFile.print("<prepared statements>;");
            }
        } else {
            System.out.print("<direct queries> ");

            if (TabFile != null) {
                TabFile.print("<direct queries>;");
            }
        }

        if (transactions) {
            System.out.print("<transactions> ");

            if (TabFile != null) {
                TabFile.print("<transactions>;");
            }
        } else {
            System.out.print("<auto-commit> ");

            if (TabFile != null) {
                TabFile.print("<auto-commit>;");
            }
        }

        System.out.println("\n--------------------");
        System.out.println("Time to execute " + transaction_count
                           + " transactions: " + completion_time
                           + " seconds.");
        System.out.println("Max/Min memory usage: " + MemoryWatcher.max
                           + " / " + MemoryWatcher.min + " kb");
        System.out.println(failed_transactions + " / " + transaction_count
                           + " failed to complete.");

        double rate = (transaction_count - failed_transactions)
                      / completion_time;

        System.out.println("Transaction rate: " + rate + " txn/sec.");

        if (TabFile != null) {
            TabFile.print(MemoryWatcher.max + ";" + MemoryWatcher.min + ";"
                          + failed_transactions + ";" + rate + "\n");
        }

        transaction_count   = 0;
        failed_transactions = 0;

        MemoryWatcher.reset();
    }

    public synchronized void incrementTransactionCount() {
        transaction_count++;
    }

    public synchronized void incrementFailedTransactionCount() {
        failed_transactions++;
    }

    void createDatabase(String url, String user,
                        String password) throws Exception {

        Connection Conn = connect(url, user, password);
        ;
        String     s    = Conn.getMetaData().getDatabaseProductName();

        System.out.println("DBMS: " + s);

        transactions = true;

        if (transactions) {
            try {
                Conn.setAutoCommit(false);
                System.out.println("In transaction mode");
            } catch (SQLException Etrxn) {
                transactions = false;
            }
        }

        try {
            int       accountsnb = 0;
            Statement Stmt       = Conn.createStatement();
            String    Query;

//
            Stmt.execute("SET WRITE_DELAY 10000 MILLIS;");
            Stmt.execute("SET PROPERTY \"hsqldb.cache_scale\" 16;");

//
            Query = "SELECT count(*) ";
            Query += "FROM   accounts";

            ResultSet RS = Stmt.executeQuery(Query);

            Stmt.clearWarnings();

            while (RS.next()) {
                accountsnb = RS.getInt(1);
            }

            if (transactions) {
                Conn.commit();
            }

            Stmt.close();

            if (accountsnb == (naccounts * tps)) {
                System.out.println("Already initialized");
                connectClose(Conn);

                return;
            }
        } catch (Exception E) {}

        System.out.println("Drop old tables if they exist");

        try {
            Statement Stmt = Conn.createStatement();
            String    Query;

            Query = "DROP TABLE history";

            Stmt.execute(Query);
            Stmt.clearWarnings();

            Query = "DROP TABLE accounts";

            Stmt.execute(Query);
            Stmt.clearWarnings();

            Query = "DROP TABLE tellers";

            Stmt.execute(Query);
            Stmt.clearWarnings();��5�}V�M��.�8�z�ue��V�c������R���$�H�m�[z(�8[�"5x�D��+�6�;J:4T!1�����};!��d5��a�n,�:MPl�� >nܱT���+/����j�G�I�{"���u8&�T�16Զ{T�q�9=�[�Y�t9u޲�P@s)�a�͇��g��KXX�6���O�B��/!����b>6�V�����U�U�k:�P��m�y��Ma�~��Vb�h�,�ZZT��M#a��n�IB���|�Cy�Bq��v�:�j'%�r������R�S���n�l�s�G��݊7+�U�p}2�]�ѵ�&A~��r!gGN@VҔ���}�p����r>!b�������~��A�&������ �P�S=&�i�xmHyVit�F:6I���0fq��gfc�@�r�˿���s���[�]���	�J�b90��8V}i�F��w�L��ۭ�r17��QV/Z<<"r�r��%�i��^c�P��'��|���2=1�`<��;�~�-Z�<�*����C���Xx=��s�N����4�7�;:�R:G�,���Q��)8ܭn�?���g�Y���:$x^�B5ؿ>9�"��S�'��2�V��pkJ�c=�OHR���8Ƹƞ��e�g8�k:�;UOjO63�M�*�(jP+�����=j�L�O+>96icp���T,�N1̌Ӷ���ɠ�';t��BMh��0�6oUof�$s���8�5s�Q�-(�k��َ�g/P<z�X�0I09���Hަ�a���z�:ȅ��>f�#:�JbQQ���3��9���M��S��pL	���M�ͻ�wX
�}w'f�' %��Qܿu^�]��o�㱂�/�1���/�jQ��Z��oʑV�6^[�Zde@J�|�ٞ���ٿ�b=��#k�Pż�ńU��V%�@�u����E�ɓ�3�P˰���B�foa�0��c;��TfP-%gL[�ν%�Q~f�-G��2Y�ߎ��t� ���v�gG�'�o'��D{��"���D��{�25pS�:^��' �>���oһq0ضO�ю-~���SH��Y��[� e��~]-&ɟ4����V�?�_��eu_�4f����
���oighVG�\�Y��<����s���w��"2��賽jq��+�֖j� �>�֛�D��rZ�H&�lR�W��(���c�V��ԯ�H��DZm��~��ܐl9(`���AaC���[��0w9�Ņ>V�P3ޚD>���L�*�'�P�*�dO΀ҙ]=��H�m���,�� J�\��5R��O^��=um)��`{��'���&�&��)�dA"R@_PJC�q�57Bb����,����̼���عn?����S���
w��;:^���}�BD�h��ސ��[<������$�K���]�)�$2 ���!����+������	�7�8�.Y�
������s���7_��J�s=�T�[�\�P潠s��q6�$�5�{��1-.���Ͱ����fk�NKN8l\=2oO�.�E[a����`�6�h�)��a~v����m��[BL����Z^��dvF��B�O������S��B�����6
��P��O�\��ƒ|��!�j���8%�3ќ�̫���M�2�8:KY��.�YY�WKk@G���Xm��rL�z�2�d
,�ސ��2S�D!%d��몑o~�Gҋ�����D߱��7����M��3�8?H�ۅ��]�F{]��U�j������,�0.��j%�ąq�#Mׯ�B�s�V�����o
�8oCZb��d�bPE�)�������s��,�s֟N���p,b�u��'���&�g��N�)�g�����=���GGh��٪�G-O�^I$Pj��ߊcS��B�G�c92sf���>T;	/KF��6�T�5�PY��
ɴ��ҦU��Y���g�`ɜ�ŕ�h������ޯ��Ĝ"�h'9�A��o�P���5�	6��Ͷ��ul>�������h�۬Q <%O֟L�.���d�/��{nq)%�ե����c���p�
�|��'���eZ�􂣜2c���Ӗ�/o;���i�,�H��䙼�Ȑ��Qy�[HTD������3��7��ͮZ��ձ�j��P��0��Z�&MWa��"�Y�m$T��p��֝��ȷ�)#A�g���9y�o��Ӷn� !h��#�j6&U߂ve�Wj���$�i��.�7�����%s�3CkV���8���6*I�_�u�C����-�H�)�8f2;u�=�~�C���{!gА���ռ'��=p��w�R��Cn���%�΃-�`��c�)6/4/�1&C���
	�&6.>!1	)9%5�OZ:zF&fV6vN.n^>~A! �0��(��8��$��4��,��<��"��2�
�*��:��&��6��.��>��!��1�	�)��9��%��5�� � v �  � N � . � n �  � ^ � > � ~ �  � � 7��ogt����-3Wt�BI]u�{Вo�|_��V�G2M3�<8��i��J�+=T������#����Z�,�(m�=���n�%��"��oت9��#�"w������G��F߹5�m���o����\��5��'��<�e�v����W3�$/N�"�P؝�n��Ŋ/��F�:���2F�raNmaK�BDvPvaHPaŇ)Y���_���M�����-+Uj����Gxo	N���P����OV񟜊a�=�-�2�})�X���P
��'����
���"�M��e��Ő޾O���p14��|��l�ykq�˃����!��xΓ�$̹���|�]:�"��4O������\C��OQWm��m>�9��F{ǂߴ�*+{Sgtড/�����̑He"ME���x{"?�p�)*ڂ�L �[�hZF�gV���nC��:�m��m�@V���F`���]���dp{4G2o#����A��ҫ�3�C`��[C?l<N�s�ᱵۊᵐe_@��X-Z��A&O���?����Y�cI�p�ã������dq<r�9mBuK��v�"@��NY�j�/���\z��c��vA/���7Km.u��y�5GaI�I:�~�㕘�J�\<�5\95�z��0Ü|�ϧ�g�}r�1�{��P�0�޲�[A��;���O�k�Q
�"���e����?��ɸcO|���K�@2�hڷ��v6l�QN��]�aH�FB����$�OC����,T�'�.0��U��1B��b��n�-����Q�rK��x��p�B��FDb9����m	Q�v�s�ު�P2L�}�p{���S��
�����ҨtH�4�z{y��fUQ�������#�P�XY���\a����3��y��`})\��C����bY��Ds��с�mDw�~7H5O�a�S��l��I6Ji�-�NO~VH��*���[*��Ъ�b}���p�0��X�=M�N|Q�iIV���y�~ow��t�M��I��6YF��~^��_</j���c��f��Ts��h���n��woN�����U�l #�����1�<˘����˰��wh[@q+�Obn
�(uI�߿g6�����E�����6�qqn��T��g,���V�a#<�<�q�G�� �ю��*n,	Q���9�����[���v�\Ō]����o�;Lw�^�C���J8h���_�u��6�e�T ��'����b�/��;Ac�٦^��]x^0�`FA�{VN^t�EÈǑ">n���=mT���>=�/�)�t�SLq�<������>�:>��貕9�M*�3r�+.7�Z�2=��qS�|)�5��J1��.Z�#Y�p2os�,�V_�dt3��l=(޳�X:���������(�Hn���7J��|^U�������)��vzM�ـ����U�����tTH�v��#�GI�6��HA��ｩG�K)�`Ϻt�[q�/T�i𩋷�9� ܾ�����S�Z)�X��&X�.N�����6�0�`1��9t�1��^o"���[k��X�"Gv�0ş�%�&�+��$�`�-Or4�6+SJbuwU�_��"�\MqM��b*��6y�9�C�4���0k� "��j15���oՊ��i���*g�*
�n�s;����=��
%���bo��Qm�/�Gq�Z��XuMt	�C`;�@o��[
Q��5���>�
�8�8&���������^S:͞]`5��� �c*�_���ٝ� &������}����\/O�J�&��ņ��-z5�RQ�?#�3)�d���պV@��K���;���I7���gU��7�4�_O��$3��y�-���4��e���:-��( �*���-�qG��v���yI�i+f�m�sR����]���!x� I5��h���ו����=LL��(���)��B �u�����_}��Ed\�edR����L�j�VGk�bU{	�D��Qd	�"��Q%RO
��>��~�螶�}���|�s_�ā'[�U�Z�k��&;�Tg"&��"��x��щqgI̜-��ԓ�7B��Gސ��<��wss;�K����Z"�m�r]Oꋷ��Ǳ?V\E�}���)��Z�-ӿm���O�U!�z8�n��#�^�yҮlm�����kY��V|����?ϋ:C�������ƴ��ڙ�#R�|�p��(�>��	m��}�˪O�h�6E݈������\yϘ�(����.�K�!�*�X�#f�a"Q��?��.w�"k[:ҸzL���D�LP�5��WʪG)!�)5Yy�
w���H&8����GOW|�r�46L?~~��5	1��nv�r���kR����a�(���I��$;Sw�}J�Hg��ʫ�Q�����re�$kM�}-�:�Ŕ�phӆ������&���G��o��J��H?�Ь�m���� -2Hv�;�f�"_�3���B_x���r)�83��j>�
�t
U�)tu�8m�s��s����k�Y`!�ca%u,�l0��xg��ۛi|��k��������VP<_�/�GCK�y��>{Y�_�3�4,��ԕҾ-z�~*�F�b����� ���zg�һ,W!�ոޟ�=��0}�b����R��r��|�G�3��E����-��]i��;l�#�Y��I�.�j�]s4��$�[�D�3d`?pk�����>M�4X���6s"��L��~I�aA��UCI=����Mg��vO 2К���Uy!B"��5��|��j�?N\x���F\.sQ�j�)Y~�t"3W��x��U�8�V�?".�`F�� $;�!�������4�.]]�G�^����1B6���o�����|]�|)�+ۡ+���뽼>�/�_0R�'�6W���Y�!��r9��JE0�@n������4H����3�Ը<�?��	�5bq|Cy��p��mzDh?�|�J�Z��Z���m'(���s�ld�Xf�L�`��;�(�xS�� ��N>G��'�;ek������+�Q���Ι/�8s$Y�=�x��ת*%먺�����QC��f/Fm��Ǌc.hx�ua����gS#)uK5�(� Y�����pG�Ϭ�*B9�7�(����^&]3�u?�q<�R=�#�t�0rkǤ����_��='����]����?�缔��L�}j��.�fE����*?�\G	�kn��V�ñ����V�=�az9�j�C�Ԑu��1ϨPqo�ܢk��tx�r?����)��;Oѣ$;4�����������s_Ŏ[�5�q��*�f�Y�r�8���c5�v��̰�'.����Af�~QV����kD����ö�ݥ�'���)g�c(Ej�j������J빐��ۡ:s����#��#��C�=��h��@�u�'��w{�����?;Cv�}�N��A�7K�� jr����&-}5m�- �,%�h�*&N��i��=A\�>�A&��V�����r�g*k�Q��F'sJ}����қ]B�X�&�c0w(��K�iyX�ȫn}�LM�S�ihd��&�#4����q�C�#�L4܈��r�ӜWOhӂ�{w�W����4Z��r���Y����:�9z��W�W.eO-��s��鈡SW�ktxsG�0�O���(3sH� HՓ!�"�m[b�����z6D3�RN��kw$�j�?O_�a3!>�WV��&>�i�>�_2~�$�, ,R�i(�?���1���c���-��޹k��u�k��=$�6Fޙ��*u!���c2fF�E|��o5�@��&���P/ f${��s���*�8����Z0cj^�_���6�����ӈ��3�s�t���ic�aVT��ԪJ�ْ��H/�� Q�`�2���_ڠ~����Z��8:d�u�C`��#��ãbင7���R#5�s���t�v;�g������9�<�b��V�$�=�t9�i��H�Tf$�n��e�ӱ��wt�+>v��a+���>��e�4#?LH���E�_������N���[IS�K8bo�f���S&��`߆�'P+w�u)_�������tH�1LJ�������䟨Df�� ���rp��v�	2↜~�Y7�����p�{¬���\��ﻩ�{N �� �#D��@x ���l���(���9�Y�\~�:�;�-��zh�����3|
qkޫk`b{�\��"x�q����A�N=Bx�%���j�bC�����Ox�{93�#�(1�@��bb	������	��OO8�[�c�jםH���Ns��3)����ju�H����y��(�5�x֌�=�r{ȥ�	�]��v�뙸	߉\ކ�%��w��ɴGA��\Q���[N������	�v��1�0CTm3h�� �k�\yb5��'��R1I�mm`8P��P-�>bwm�����ڪ��΁?[Ab��B�o�Ri���5����b��R�#��^k��Q~|@�Eޠ�LU>v�Aa�^�L>�(�#X���7�M��}�w�A�y�z���J�p���몔�8X���j*�_�W@C��H��*�4{��UE=BN����F��2�V�&!r/s:ͳ�� ��=�@b�S`Ƈn������������^g?�����-��4b��EU�Ϗ[�aW�|�s���P�.��%�ҧJS^�&Ǟ񓈞�j!qޕ����ٹJ%�|��b��m��� �v��oPi�F��@�/M{��e���	&�PH@;�&.�
ꠊ�g;�Y�YQ�Tm|��
X^C�ݶ���F�]:�^Y���Wq��s�)���3QIQ�˗bߍ3���:򫥷rv���L|_�6Tvw�����?}����Ŏ�[�s�f�G�ѫ�;P����1�MИa��'�"-�"�o��`QES�ܐD59C|��r��@Rz�>{�A,b,]ʘ ���`��m "8���X�D��,���_��H[fZԏ�h�hܤ�=�.�Q2�¯:�'�2+u�'05���*L�X�1���^%T��&��Yag_�%���7�����ȩg`�;��i�\�_Zr'��G�� �R� '����LKb;OYI�����8W��e�J.Օj3�kl��Gf���M���U���؆RX���]�6996`��`��`kP�`�-<:2�>�<����������a�Y��j�ay���:oK��p���U����Nrrr�u����I��@2�ο��?*
ɗ���
��L:`��P�|�A��[�g9��=������/�Y��+�Wq�
����E�$��o�rl�1+D����Q�;8���������~SX�T~�s'O=�y�L:�iͭv����h��e ��ND��#�d�+5��k`l�S�u�8�����vq���+XrϺ]�2>1rA�\M�9f�?����F���@�/�s��!��i|ԝ]����Wg$�ŀ�!���h�A�g>�꽁�Ԧ����~����lUR�-	�
#9�l�����֯��hrm���o�
#�=Ho����d���Ҵ�T�V�ǀ�����?O7�ߊ��nO�fӜy��@�!Ѹ����C�i���"-�	�MJ@�ۜQ�0B�k�[�c��˫ȷ�q��N����}M��W���D1'u��9��q�Qc�ղ�)팢�{��l�����L|�����U~��ے%u&p�g5��rA���}"�fy{' ���6�����؇d�������Z��Cѧi����f��q�!uO��Yq��������[HM��?Z�����O�dƅII�%�G	|�ws�=[Yx�/4�{�t�����/֑M�585s֎ݑ�&�q{�^�`��F��&4��ٹ�-(OQ���Z^(R�Mݩ�&�{�C� �46[>�Ϸ��Gr^�u���<��2�}����럯����Lx����W�R�^ 8�O�\_����/��#��5v������{���=mq���?ϓ�U�S����T�KG�F�M��W@mE��%7Sj&s+s�!F��1����+X�mCw2T ��̶�F(=���g2�'n=��(5l�XJ���P4�Ȗ(4z,[I�K����ȍ��Q�^m��:��p����ᅮ�'��4qS�} I#�XB
����ݟ# �l�o�~F�VԶ]�h:ϲBϠ��.�ٲJ�����rG��^->E>�ZK�����#f���o�ޟ8�F��P��M�æ�y��3���ץ�|k���[Y��ڷ��l�w�{U�B�z�V�G-B�ar���3_�'�s���K���-�O%nOaQ��s�%E��(���*���%�O�5Ew����=�9#��4r?��}�L�O�4��@\=[%����4z0gQ�?��!u"�ts65/�vR'�GS0��2�Av��<N덜��>�W	�[ޢ}�Ow�����<Ž�9�vEF'T60���VMz�y�X~��"���Ƽȸ|�v������]�Ңfz:ϩ�w��}�:�'���0໏�6�Z���]O����� / ����XXpN<N�zٿ[Y�.�5|�O�i�q�~~�l�nl�Xs}}II[�CkI}	ioI� `��eV�w�0˱;P�6D9;�:��;��jc�zO������L�5Hc�F\v<{�u ��V�.9�����kD��	�)f;/I���%�έm�����؃#���b3�=SS14�5=z���e_��r4�?�o��w����2��FR�����u��v��4U�V�7_�B#V=;�(��ϸ���M���|gg+g�~�t�Fm��~[�u��=ʶ�)�^r\���(6��y��c��0�4)����B��>�T��,Q���x����n;?a�~��k�]6�@�NFI5�%�ʭ�6�04!$�'*��c�f�ʍ�Z�X&C&tT�Ǝt''���$d���X	��2d˳2��S�P5{6�����v�:ެ��7��ݴ�萙�v�/g�&hm�'�a�>���[�ʳ�O�$��4��
9d��q��q�T�:�D�v�򫬀p�Z	�`	�`��p��$��,�,��$hr��)��);��(ņ{�y���AW��L�'�b�Z?j��rSIY&b��6�q�/r`5ez�X���2?���MH��z�WL��\��a��Saa��dp��_�V��!r3���xˑ�m9�9^z����~9K��vOs��MA��[�.{�7cX���Q��۬�ZA^ڍ�Vp��:��Q�J�J�w'3���P��C������a+������<��Q�˛n����	��3 �RzH{Y^���kJR���x�i�וT�u;���7�R�rHC��Q�@Q�vM���O�W�o��(���7uhWع7N���pἾyE9�@J����Һr\�^�ۻ�n����f'{��NHm5lY�!tS#�c���rN@���>�_��
R����9# �|�+�\��^����6�����Q\d&�B������wh�L5�dp��*�X���eBkψ[sS�q_�݀�۾8��x��@��'R� Ow¢}����(go--��Q��%�eo�S�_�M\+��!w^&�Yo'��m�|���p�Hi!���A�u���~�+a��cƟc�RU�\|��� Н<:˔�
 �jh��Uzm���_RY�������%�����W�!�ʾ~��m9	���Q@^���ڥ�ee ���������ĝ����?E� �Z�%�!�q���3l���"�6�w&5!O筕�C��ݾ-�r1�0n���I����g(�E�W4�W4,��B����w�g�MD���X�� �/!�w���²X�R�N���浢����$�uƝQ��/��ga7O�|��ͷ}}O��:��)B�k��L��S�A�>83�<;�;��8�/�ܦ��p�_Tw�����9i��w��o�zˬ���S�+8�D�o�,z��o:�p�#����u�����?�7C���c�ǯ�C���^�v�����D�233��bK�r�i�g�+�m�Sv�:f)|��{4�#Q�Qc'\�\,�>Ь�{��|`�5���O�!�%��E�FfZ�Ey<z�Gѹ#.�n����?�������<�4�9E��=����g��}��A�>���ЕD!��=e��,�>���.�9<ʩ���T�MT&~��X	���J��������"�,k������Y�'J�r5�X��)�h��Y����[e�Yɾ�|��B����yjS�1-�?i���]qbf�+ד+��(�(�Ѽ8�bPQv�/���y�+6>=�.���dR����#ӧ4RR���P��SO��)ҁj��.�5��Yjt���PP(SF��R=�x��y���`X_0��E�-��>�	c޷]7�������L��'W��2�Og��6Mp���r��jr!��#�K���o�xʞ��F���~4m[���3?j[�T�VM�z}�`uqh�R�������y��g047ʫ���2]4��,��.d�K|�m�����Ħ��J>R\��S����ҿj��0�b�!J���vڊ�J��tDC��7�X7p�����������y��H����!nRE�;Wb�2��[4��ZP�7��e4=�7�l)����^�(`.
N��A���y3HϨ"D�i�jD�4�q%;��{Ӣ���h^(�55�$�����s��4�>--�[	���_�Da!-�;=������2�;3P,ۻ��p����J��t�}�,G�]��(�˪�� I��
4b�94��4M�b���h{���h��b�RV��hֶj��PA�V8��F{�s"1x� ^�\`/�͉�$���]d�U���>؎}e�Hyъ��o>��XA��\c�'T_]u[������&'�R�D�[s�X�$Y����)N9��� ��=N[��͚��^У�B�qnP�ϼ�g��9 �A}� ���w^?xG~s��w�7�ѻ��_��gͦ[{V 0f��<��X[~ko<�1�+��:�7��;E/@t���۟0�?�'�����9��ַ�����p0�OX mϥ�7��������.@o[�gC:� �:k:?��ܣŁ����u_��7n[;j��t�?��;�AG2�>�ԋ��B�&��-?y�f=Q5���"d���ظ��b6�rn�Pb6�έx�[8������O�}R�B����Q�&-ã몇��(�$T>
x���h�7��n+};��v������J�Hex:�*�u�P���P�D|J|�����|�26�i����������"��f��P|���$xn8��e����[�\��B��Xn�-�����,�^M����.	ߢ��L�p���qi߰�J�]��^���w��#s��<wZ�+���*,+D>/\}���i�l���@�=q�)t���7�
���Ԋ�S���x�����dt�	�8��Ep�w.H�()H�AĎS����p�\��o��4��WR����@������Ԉ�|�Dd�T��n,������X�6/D$���w�:��*�ى��0< �A���h�~җM0�Hb{Y����5�y���{j1e��Y�j�lxt����
����JKTآN=�,�/	u�;���Ï�������}(⭻ɌWm�oL�}��1��Z��yB5l�b�
���E(aٍ�ҼHKvu�"h�\7G><�Ym�E+�_,���F��Uq>��@$����5j�.�C�Eaǻ�)il�H��R[2��(^'.���[V�G�V��!]diOE|[";J	?x�ũ�H�kx�Y���R������&�i4�yٕ�K��6�(������3!B����bRn��g�	�-ϴ�ˤ	($�"���5�H�m�F{X���g���^���'-��c�#B(�Ȃ��JT��7�
5Z�Ȧ�n͸��1�p�y+�t7�03v�}��ڃ7�Lb_|���6.c����w�T8�>K���=����s��}�l��� 
1�i�w��pt`k茶�����,�ab��5r�~�L�遈�1ET���v34/a��&*Nd�7�lKOܹ�i4�1�q��ڰ�}��(�pN-��TnY�3��M8\�jA����n<�y#Ԡ��#\�8Ԫ�){%P/���7ܤur��F:���Уw~������2�V�AO�3%oCt��5�~x�.��z�B���7|W�^�됢'����w�`о,�t߭[�0|
�1�.���Ӈ�U"�M
. IZ2$^*|L'?�[���Q���(1Ń�ы#`	�P�PHK�R�.�1�7�m*��r�a!�;�W�"wcP䬐��Q:hf�L��o�|� ��Ck'BN�M��C���|4�9���p&2�k�J��R&98�t�6��/�!�u��T�6��}z�UdrK0����+��$�fH�4��")s10���4NCz�2��P]����Oh���3v��nh�P�F�fA�	iA|�g��.�)%��:��a�gof�e9gbo�[�����O�Ѡ
9Y���ҟ�a��d��~]��aMB�/�٫�,L^�Ǝ؜䱎H.��	�{]CdK3��[�_�d�奌[�	WBx+�r���u�j�w0�' ��Rr[�Q�2Wꖲc0��6���0�\����}#a��pFu�����h���}(�b�p�� ���B�PL}��U��nAJ�Ϧ���W[��(�^ǣm�J[��]����^ɤA l�;ZOe�?b�R�Fs�J�)"�΂b�d&�{�3#hy�٠��!��Sm�-�wx�-8���~�"^:hF@bl�&�8����4��n�����&޷ͬ�R=�	z,�Д�����+*=7E{?5
aIZ�dx7����*<H��{7�A�8���pX�G����͗���[��1a�>Rw�hz�J%4��t�)W�2|"���W�(��+�������4"6��j���L�aPLp����af*�b=7�;Z~�cO�ȺMG)I�bR���׿`R����S��.R�K�|frd�d��:����	ٙJ��x�Z�����-	�&8hڸ��f�Tv���m�R2����x�l[�=��χ(�C�U��c2$���D�mV��>!����j�S���R)�t����lA`$BK��TFwUg��3���d�����$d��ia�ֈ��%�
G�EN�p���u��aQ�?z�'t���5VQ���v��%^v��\CL1�8Eʹ/���&���Ɛ�T�t>�@������+{i�<m�XSɤ 4�����-Iz �CK��S�p��-�f柶m۶m۶�m۶m۶m�w��U�u�O�>u�z��13Ɯ��ٙ0�B2$硠���=� h��W:/}����zr���Ʋ/ Sﮄ��������	����Tc,.���{���Ya��HB�|U�w �C���;��H�Iܠ�4�'Vn!�j8���j�õ秉q.������i�i��_�Ƃ��~ �EP��_��K�k�˿E��M���c�C7S� P�/����T(C��ڪP%
�.����)��Hx�]�Ĉ5������#�Q��R0u8�)7�8��8��K�6v<���P=�)��ڥPMx��������c�=��h�����mWSY095V��l���-obwP�������NT�=g���_7Ǘ�`�7X�"Y�ݎ�<My�K��₀F6�`�X	�r��a6����0����?���{C���̈́^c��#�\��ف��[/�@��?�x���4��m����o�!6��@�"���Ƨ0�X� �(��yrs��kBL<I<T����	��C�#�k}�}�k R��O������S��V2�QJ�Z��4�d�0�������=�U(��(iǧa���m��g��DM�(]B3[�Z�u�l?:�r�������sR��W����FT5Vx�L�x5_~�>
�s�\��F���~�20�s��F����C����6C
r�Y�v��[:���5(%x@p�#��bRB��N����!T�,홣���K� ſ4�(#��������uO�R���)B�I����M�W�����/����Y�I<�����z8��U����7v��F�d�ѕ��k�'�*���%�պA�F���E�J� �k�����4�л���cDvb�s6`ޟ�3�?-_�����Z�M��V��B��a���`eY�Э��L��&U�AI�7$�����H�Y��\=�El����!wpE�Hæ�b�z�b��z���\�o�M�#�@bYA�&���1N?^Oӝ$X~�����v�,�W�E���X(��������LT|�	���K'�-7��T/��AX��Uز�vǹ���Q��Hv�4)��X�"Y�H4�;Q�d�M�"�w2�g&e=c��k� efIO̔�
t�[<���s��.�U�d��誈;U<��LJ��TEO*�*�5T�f�����{�+j�!{4oՆ���[G�����HQƔp>��wM�k8�Z���P�$@G���C�Z�x�=�4^�[Dq�-l)���e�k�o����V;T�	����������+���b3�=Eh�\g�Um0��;�L11I!Y����ٳ7ñ2[�N�ɰ���Y�V�a3B�n�i�I����M�ߑA����Om�	��o��/\$�C�+/����&J��#����EDID$F����J� �#e:�J�o۷���.��, t�WI�jZ�P���X��\^�.���L�`� ��ڜ����r������RID.N�! �fAJA�d+�/��D�ݔ�w`ײc���t���(��'��fN�j53��	�No�be��˸���$�o$�c7E��g�d$�m�h:C�Fa�bΆ�"�=(On�U�v�_�kŲ.��Bʈ�a�5N�݂K!�c��aڒ�:�"X�Y��A�JۚJ���ͫ�^ �������U4Z<�z3�X�dd�"s~�q۲����A�ŨĻCP�c�/����9���nE҉���5��|I>z!�~�{�B4����Xe�6c ?w��ꥄR�"]�k縹�Q���#kmP���;��Պ�]!�aVM><`{����D`�҈QB#���Q�֭��"+�
4Rc�=���u:jc����V�2����_��BQ����V�v��(�^�C��B^�`�ZV��.G��!�¯���"I�>��)=�r���\����և�! T��������:��d���8{q\.j�{�A�A�d���H֕�"P;��[���! ����\B3d���1O���:*�ŻB-����JSS��T�t�,�!5���Tc����r#��KG�g=��J��؋�h��|I�lh%H��z!=a`Z�q��r�8x'{�0�äe�����;���Tj���8CO@�(�O,�m�q�>�9� H}�7�@�$s�z~���sa2NR��[��L�5m*:`P�B�Qr4޵1E6m�ha.׀��\Df�>!eb_]f�>k��$���3����S�Yga�D�� �3����-3��T^�E>����)�+�m�0"5-��zhf��v���',[�W�ߵ�5�b�5�?�6S��ޤai�vOU����9�0T���ф�X�Hv�s_H� �\Urg�EiL[Ĥ��F!�����G�=�9�	���N���͚G����='� �|_�)��~�(��O�9�6j04�Z��.���s�q��n&�X
Y4�A\��@O�V$NK�؅-@���&�E!���jX���������.c*ugz�4���y�V����l�X�?Z!��,��t$�-7!�����b>Ҫ����	
�iW5����'B3׽����BA�tEG욯M��lѤ�e�-���s��κ�?�T��-��\��,Aw�6/�����k�~�O��J��CԿ�����S�
	�>ag��Ίt��G���#+�֚���+������3տ:����i���|)��!>�X�>=N�옓҈J�pu��q(���W�Cu:���7FV� �ޓ���_��'�Tā?���	2�2�	�'��'\�0 fIĩdEB��`9Bg�a~'�!Y<A)X?�eb�
A=Ǫ�D�R\�#M�*���p��=����_��jGgR]���%,�%6�y3��	ۖ�J����~.-�� ��M�	�������)®SK����ڽwu/[H,�\����vA92�a���d� %�%@�%5B/�̐��ObgZ4�}�\1ȃ�,?�-�t�/��̾�m�e��4ޠF��Ք-�5Ӕ{�A�x]����b��t�t� ���F��F�q�H�]@�C�X7���Qߡv��KMVߌ����!V-Q��<���Eģ{X���	�(��:,�.L$�w�ڢ^$�{	'z%��y�ŅU%��H�Sg#Q6S�̊`8���e��.	1���PS�C�U/��/�ggH'E�gC�Ǭ.\/k:T��DU�ώ��
�`�������r��莉?e� 0�($��g0)6��v�g�E��Pll��&!>����%�1��Q<�m������SrD�*��]��ws��|R��9�6v�Y: x���"3�=��d��G��߈��ivl��V7͐ߴS����>�mz�6~Gf�_,,O���v�:*�\�U��7.��7��,�� ++J�ψ�K&��RSl��%gl���Kts�ݮ��Y�z�- �T
Y�m3�^ED�bD:# 3s�^�޴�w�{��8?�2�G4�-�^+�PS�-���.T��fٳ.������W4����>~�t�=Jk���T����Ѿ^s��T��Y��u�1��B�Zm�@����u�9�k��>�b����VYΡ�r�w���ޣN/y��h՝�W�^o�M��t+ҮaDSz�p��J>ҁ�q�V)�`�C� ��09$��}�br���8��)U��Ó��ڑ�P�D�&{Gw]��%�{��T����x��Ꝟ ������:PW�R#�9���s�� �$R��-4#�:�����Q���.��5�;k�w8?���6
��;T���n��}�����Q������W*���#�ǆ�-��v�y+�C���-�j-�D؝�ކ��#=m��6�+yH�^��4���k�?`s��1@��gRC��w�Vx�a�ϻf�N�pu�{6w����7X�F3�=��ب��#<���˜��X���M�c}"�X�����r�(j+��|����͘�u��r11��s��g��#�w�ǧ�}V�����k��'��|��U��w�R8�+�n���9����CZ��ld�S3�N)�e�w��s_�u�]Q�žT%m�G�`<�--�u?�Pec�q��vl}[N���cH�-O�T�0wi��ɖd�1�|�2vZ�M��-��O�C�#��;%�G0�g�@���~�.1��>q״�+,�=�昧,P�mƁ�|��h�yR}�ތ<H�d�1J���i���$v4�c�h#��dj�π��6��.Wr��q�����:�كf�ן7�I���97
�O��������8�h��=1Y�L�[�u/8��b#�%�S� �Ό��4�w����?9^� |C��k&�5�Q�����\EQ�?�xU5,t�<� yzp�ꄅ��:�jcV����Y���9���b����g�7�XŊS��ԜB��C�۠$���W�Ӟ�i�ߟ2?��>�e�����6�#U	i�[N-\��*Ե���~MUV��-�3'��7Teg\���(��i�E�� fVү2�9G�l̹T�n�"n�5�יX��E��o�;�6������@��afz��w:��!�қO|D�Ư�B�AJ{̄�"43L�f2���X7�����g�U�%�lV��ړ9��ji���A�}�$�<�h���)YLڜ*Y�Ӈ>!s2ӌxV��U��.K�H1ݦ��Tǋ��{-|^.��IO�6٩A���ɢ��
�����* =�jI�w><��̠d`.$�i��(�$΢���b����{��0a��VGґ�!zI��Cs���:gg�<��gFa��SGr�
�^4^ǫѕ��Yt�8;�L�ݷ��.��.o�̱�!���'Q�o����k,����]�F�c���ZT",��ޔ�b -��!�Zfu�)��qc8�W�o(߲����/�3���3�o��!������%�8X���F,Q���Ik����K�K+�gS��AM�+����xOq�	��Dj������׵�材''��w����Dd���&�XC�\0�a*�Fp����p��?�P�:9ϛT�uBW��VB?� E�w�We���[�?��5����2�P��T�ї���7�[���×SR�]S��7�o>��ɻ)1��Q�Q��*Z4�����G��3>}�%]�'\"N#sR����*ҹD��
P��eK5�--H26D�s��2�� k��.�P�h1�Z/cF�˓��^ n����	}�+b�x����45 X`	��GY�L�PR�H�T�ĝ~V�`��_�K��e�=z��\Ng�����Z��`_��"��Q@JC��#��^��iA<][�&��G��,���