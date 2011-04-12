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
            Stmt.clearWarnings();ïœ5å}VŒM¢È.ò8Âz™ueæéV©c·½úÍûRúïÁ$“H…m„[z(¯8[­"5xåDéº+…6û;J:4T!1ŒÂû·ë};!×íd5¸±a·n,¯:MPlüÉ >nÜ±TÙË£+/»â”Çëj¢G”Iâ{"¤Òïu8&ÜT16Ô¶{TÅqÍ9=§[¾YÈt9uŞ²§P@s)°a‰Í‡ë‚ÂgÿÕKXX¹6Ÿ —O”B÷/!şù›ñb>6ğVìÏ°›ÁU¢U¬k:ïP‰ˆmãyıÑMaˆ~ÁVb hæ½,ÏZZT“šM#aøèn‡IBöß•|ƒCyBq¡Şvƒ:Ój'%§r‹·©¡ÉR¤S™ĞÔnñlŠsšGãÔİŠ7+U£p}2Ù]†ÑµÁ&A~Š¹r!gGN@VÒ”¹éì}ûpõ©å¹r>!b»´î¼¦ÍÙì¦~·AÙ&¥•ãëÇ ‚P›S=&i¤xmHyVitšF:6IÊü£0fqƒõgfcó@ÍrÉË¿Ìê¨Õsùò[í]–×ç	ËJ®b90’ÿ8V}iœFwîLû²Û­†r17º“QV/Z<<"rŒr™’%Ïi–—^cÓP¡”'Ï¾|™–•2=1İ`<ñ;§~ˆ-Zã<©*’§°ñC°ÃöXx=•Ğs‰Nüí ´›“4İ7¶;:œR:G ,°ìQÕ÷)8Ü­n?ÙßÂg˜Y„ƒä:$x^ÇB5Ø¿>9ë"¸ªSğ'Íæ2ÒVÉpkJ³c=ÉOHR§°8Æ¸ÆˆùeÊg8”k:Ñ;UOjO63ğ¤¦MÒ*Ô(jP+ïŸ°êÆÊ=jÅLÇO+>96icp‹îúT,ÆN1ÌŒÓ¶•¯úÉ “';t¥±BMháï0è6oUofğ¦$sæ÷­8½5s‡QÃ-(ÅkãœÙg/P<z‘X±0I09 ²ÿHŞ¦›aŠ‡ä§z½:È…ı±>fé#:·JbQQ½ù¸3œ˜9ééâM‰ŒSŸÀpL	¹„M¹Í»õwX
¿}w'f›' %¢¸QÜ¿u^ ]¤‰o°ã±‚•/›1Ÿ¯ì/íjQ‡÷ZõšoÊ‘Vñ6^[¾Zde@Jú|ìÙíÊìÙ¿Úb=ş #kúPÅ¼ÔÅ„UÄÍV%´@õu¼ŸìEÚÉ“Õ3§PË°¹‘BÆfoaŸ0îÇc;»ĞTfP-%gL[ßÎ½%±Q~fî-GŞÓ2Y»ßü½t¥ ®˜ìv„gG»'°o'‹âD{üá"ŞÀêµDææ{óš¼25pS¼:^¶¸' Ø>ŸÒğoÒ»q0Ø¶OÄÑ-~ÄÛò„SH¤ÇYğ€[¬ eµ§~]-&ÉŸ4´òù²VÁ?å_éôeu_ò4fâ‘ôÔ
€ÉâoighVGò\ÛYùş<€»¬¨sº±«w¿"2 ¿è³½jqÂİ+¹Ö–jèš Õ>¨Ö›æD¹årZ¯H&‘lRÚWó®ï(´½¢c¢VäÔÔ¯ïH©§DZm¹~£ØÜl9(`®òAaC¬ì[”¿0w9³Å…>VÛP3ŞšD>Úì†ô‰L¸*±'èP·*•dOÎ€Ò™]=¾©Hºm¥÷½,ÉÕ Jò\¡é5R£ÈO^îô=um)ìâ`{Ÿ´'”Øö&À&°ê)ódA"R@_PJC´q57BbŠ¤ª,šß¤Ì¼û†ÛØ¹n?”¨Û·S²åÓ
wÉä;:^´âØ}£BDÍh‘ãŞ£[<ø‹£°$ìKüş¯]ö)´$2 ğÜç!À¨é+ŸøìáğÌ	‡7ä8î.YŠ
áô‡Øçù¦sù¤À7_Ï³J“s=şTà[ì\÷Pæ½ sêÃq6œ$ï5§{„§1-.ÏõÑÍ°ºÈ–©fk÷NKN8l\=2oO™.ºE[a°‰Æ`Å6h›)Öİa~vÖ•Ûım°ö[BLÆÂÙâZ^ÒdvF…Bã›O‡Úø¿ğS¾ªB„¡·Ÿ‚6
íÑPÿ»OÌ\¿·Æ’|‘ò!åj®Îä²8% 3ÑœÌ«ş“ïM¤2Ì8:KYÁ·. YYúWKk@GÎİÁXmÁ¦rLåz´2¼d
,„Şô•2SªD!%dÍÀëª‘o~çGÒ‹¦ŒåÂäDß±²7‘Òë§Mê3½8?HÛ…¨í]½F{]ŠîUØj›Œò—ı±•,ß0.Äëj%ë˜Ä…q±#M×¯¯Bís†V­…àûo
8oCZb§d›bPEç)º»“‹•»½s‘Ë,ÜsÖŸNâØÙp,báu¥ì'ÖìÕ&Øg†N‡)¯gí¹½ÜéÀø=ªÀÒGGhæúÙªÛG-O¼^I$PjşßŠcS™ıB•GÌc92sfóÀ™>T;	/KF‰ä6ƒTÕ5ŞPY”Æ
É´¦¡Ò¦UÆÖY¯÷Ögî`Éœ¤Å•¦h ËŸ‹ÂŞ¯õğÄœ"÷h'9±AşàoÈP¶é5ú	6¼©Í¶£Ûul>‰èø™ù€ÿhúÛ¬Q <%OÖŸL×.¶ÔùdÅ/À—{nq)%¥Õ¥­õ‹šcöò«pï
¤|®–'õÀ˜eZãô‚£œ2c´îÓ–Ö/o;³Âñi×,”HÀ‚ä™¼ÊÈüÀQyß[HTDİÁ´½¼3õÛ7…’Í®Z¹ÛÕ±àj§íPˆÀ0‹ÑZ³&MWa÷Ş"ÏYÙm$TåüpòÄÖéè¢È·•)#A¹g§Ñë9yô€o¤«Ó¶n¿ !h˜#¥j6&Uß‚veWjÿùè¡$ŒiŞØ.õ7ãèîÎğ%së3CkV¢öØ8­Õü6*I†_óuãœC²áâ›-äHÇ)8f2;u£=êº~ŸCæò†Ã{!gĞûáÕÕ¼'‡“=pŠ‚w–R¿äCnìÙæ%ƒÎƒ-¢`îöc¶)6/4/ß1&CôÌœ
	ä&6.>!1	)9%5ÈOZ:zF&fV6vN.n^>~A! ˆ0ˆˆ(ˆˆ8ˆˆ$ˆˆ4ˆˆ,ˆˆ<ˆˆ"ˆˆ2ˆ
ˆ*ˆˆ:ˆˆ&ˆˆ6ˆˆ.ˆˆ>ˆˆ!ˆˆ1ˆ	ˆ)ˆˆ9ˆˆ%ˆˆ5ˆÈ ¶ v ö   N Î . ® n î   ^ Ş > ¾ ~ ş   Á 7õ¦ogtÃóåÃ-3WtÂBI]uú{Ğ’oô|_°óVëG2M3Ì<8éiÓÀJ+=TÀ°î…Ïï·#øÖğá”Z“,Ô(m£=½¥‰n–%ïä"¯áƒoØª9ú¯#¤"w¤‘½ŠöGÎôFß¹5™m’šıo‘¨š\¢5±î'Úö<e¾v­‘ÒÕW3ä$/NŸ"æ¶PØôn±·ÅŠ/ÜäFË:½Ëï2FâraNmaKì†BDvPvaHPaÅ‡)Y¯á…ó_ÃÃãM‹ÅÃíó-+Uj¢ğâíGxo	NƒËúPæŠå¼®OVñŸœŠaÔ=·-î2ÿ})ñXÎÆÔP
Ÿñ'Í¯„í
­ÿ"‰Me™µÅŞ¾Oºü‰p14ˆÄ|íõlôykq¬Ëƒ¡°·ß!éêxÎ“º$Ì¹„û•|Ó]:á"°ú4OÖ¬çşÃ\C“OQWmêµŞm>ó9§ïF{Ç‚ß´Á*+{Sgtà¦¡/Ÿ£ŞäşÌ‘He"MEâ½÷îx{"?ğpş)*Ú‚L §[ÀhZF©gVŒ…òšnC‡±:Ïm–»mİ@V¿‰†F`½ÍÏ]¾²údp{4G2o#†‡¡ÄA…˜Ò«ş3ÊC`ã¾[C?l<NÈs¡á±µÛŠáµe_@øãX-ZâÏA&OïøÏ?¥ØäØY½cI¹p•Ã£Îêİş–¶dq<rğ’‹9mBuKúúvÀ"@İÓNYÏjÑ/Ş°œ\zïÛcÌŞvA/Åáã7Km.uéßyÖ5GaIˆI:~ã•˜³Jş\<ñ¬5\95Ëz…0Ãœ|¡Ï§…gñ°}r¯1É{ŞûP‚0¢Ş²ª[A‰¥;ºÀúOîkšQ
ğ"ˆáøeü¤÷ï?”ôÉ¸cO|œşÖK“@2ÉhÚ·³Ùv6láQNÅÙ]ÿaHøFBƒ‰Ã$ğOC©™èì,Tá'’.0ä€ÒU°™1B¢·bù–n¹-‡¢÷‚QêrKô¼x€õp³B­ÿFDb9„šÓm	Q‡v­sñŞªP2Lç}€p{ÄÂÃSœş
ïñ£Ï—Ò¨tHì4óz{yô¯fUQÁÿÓİÂà„#”PşXY‡ü¸\a®ôÒò3yÔö`})\ÜCèÿ±™bYü­Ds¥œÑâmDwû~7H5OèaˆSšàl¢…I6Jiş-ÕNO~VHûÚ*‹ìı[*‰´Ğª²b}¦‡¡p‘0ú¤Xà=M’N|QşiIVÒˆÄy«~owéÇt–MñÁI·6YFû—~^ıñ_</jäÆèc×Õf ŸTsÁëhù÷Ôn‰ÆwoN¡ÓòİûU±l #‹¥‡¿õ1¡<Ë˜ˆÙÊñË°¶£wh[@q+©Obn
(uIäß¿g6Ìç¨ºˆõE™ÿ¸ãë6ºqqnÖáT†¶g,…¸ˆVa#<©<šqªGµê ë©Ñ¼Ï*n,	QØÑó9¡İê[¿´vö\ÅŒ]Åİ“µo‹;Lwµ^¢C»½ëJ8h¾¼²_u¾¶6–e®T õ'÷Á«b²/ñİ;AcåÙ¦^Úì]x^0ß`FAı{VN^t¹EÃˆÇ‘">nÊÄè=mT§ª>=ú/¿)ÏtáSLqÌ<Œ¼œ¡“ö>Î:>¾ è²•9ĞM*ç3r†+.7ŒZÏ2=³µqSè¼|)£5¶éJ1²ê.Zë#Yûp2osş,ïV_±dt3´Ål=(Ş³œX:¨ÊØîö÷¤ë(¼HnæÃË7Jıë|^U™¡©Áı±¯)š­vzMµÙ€œîÜîUüËÒåÂtTHùv£ä#ÇGIØ6÷ŒHA¬‘ï½©GK)“`Ïºtñ[qÍ/T·iğ©‹·™9ï Ü¾½±”SåZ)äXèÍ&XÇ.N»‰ŞÑë60ù`1ùŸ9t˜1Ùñ^o"¥“[k³ÌXĞ"Gvç0ÅŸ‹%ñ&É+¿ù$ı`Ÿ-Or4³6+SJbuwUŠ_êÕ"ù\MqMÄb*´§6y”9õCİ4åÑì0kı "éúj15íşüoÕŠâÉiƒ¸â£*g—*
’nÑs;”²åØ=«Ù
%îóáboı’Qm“/ÙGq¬Z® XuMt	—C`;µ@o‹Ù[
Qïà5µ”Ë>
‘8¯8&‘¬†ú÷èğŠ·^S:Í]`5¥üä ¤c*Ü_ÄÊİÙç &¼”Äõ“}´µ¨®\/O÷J«&‚…Å†¥ù-z5…RQî?#÷3)ˆdú¡ÕºV@·ÙK‡ŒÌ;¬ªI7ÅóÕgU€¦7î4÷_OÇê›$3¿©yÁ-ÄĞÑ4ë×e½ºç:-´( æ*¶İ»-×qGòùv¤•yIài+fªmÛsR¸‡Ä™]ëåÊ!xã¬ I5êØhÛØù×•«´§ô=LL¾ë(”õ)’•B ·uú…¡ãâ»_}èıEd\İedR«øıçL¶jİVGkÖbU{	¿DÔáQd	Í"Œ”Q%RO
­ï>ğÌ~×è¶µ}½¿—|°s_ÍÄ'[ÙUİZİk˜ÿ&;ÀTg"&¸Ó"Şñx¼âÑ‰qgIÌœ-—òÔ“Ó7BÎÛGŞŸÕ<•Çwss;İKÏ­œ“Z"½m¥r]Oê‹·¬Ç±?V\EÇ}Ëõó)ŒZœ-Ó¿m£÷ÃOóU!Åz8›nÚÒ#—^ìyÒ®lmüöÓËúkYµ§V|‰Ÿîû?Ï‹:Cí¬ö¿¬›ïıÆ´ŞˆÚ™­#Rˆ|¼p‰‚(Ú>õš	m«İ}‡ËªOh‡6EİˆíåÇŞÍÆ\yÏ˜à(°œŒù.ÅKŒ!Œ*àX‹#f a"Q½Ñ?æê.w«"k[:Ò¸zL®µ÷DßLPê5ÎîWÊªG)!¼)5Yy®
w¹ÆÊH&8ˆéµÔØGOW|ârà46L?~~¦5	1óïnvÒrÀÛùkRÙüõòa×(›£ÑIüÜ$;Swƒ}JÆHg´ÔÊ«ìQ·öü»reñ$kM­}-Š:üÅ”phÓ†é¿Ğ­›Û&µåG˜ØoäûJØÔH?»Ğ¬ÏmÅéÿ‘ -2Hv¶;ñf¿"_œ3®ä‰¼B_x’¦r)ö83®øj>×
ğt
Uş)tu³8m³sªôsª´³¦kïY`!ëca%u,æl0øÚxg‹©Û›i|üék›¼ı®œº½VP<_ß/í¨GCKÎy½>{YÙ_™3ñ4,’˜Ô•Ò¾-zÜ~*ËFé°bˆ­üÚ °ªçzg£Ò»,W!°Õ¸ŞŸ®=¶ô0}b½àîŠ¹äí˜RÉÀrßĞ|®Gÿ3„éE¢Ÿô³-©«]iˆö;l™#ØY”™Iê.Ğjâ]s4ĞÆ$[ºD­3d`?pk ”±ˆ–>M¾4XæÄÀ6s"¡ë¾LÍ~IÑaAÃßUCI=Á®²ÖMgŒÕvO 2ĞšĞÕ÷Uy!B"äÄ5¹‘|ê×já?N\xş‚¡F\.sQÍjù)Y~´t"3WÌÕxËÜU8ïVÄ?".«`Fú– $;ú!ŞåòîË÷‹4¾.]]“GÎ^²ÙËÀ1B6ÓoõŒ¬‰¼|]î|)Ù+Û¡+ÿ¼úë½¼>—/ä_0R¦'„6W•¸“Y‡!ö’r9€ãJE0Ñ@náÓÊÊÔñ4HœĞşÁ3ÄÔ¸<¾?ãä	½5bq|Cy¨p·mzDh?£|×JİZÎÊZçâ†m'(ìÔñs”ldXfüLë`Œ¹;Á(ùxSŞò ¦¨N>GïŸÖ'À;ekÑİÆÖçù+ƒQ¾ŞÎ™/¿8s$Y»=øxÙÒ×ª*%ë¨º­àØQCˆf/FmÂÊÇŠc.hxÆua¶«²¯gS#)uK5Ş(š Y”Ùô¯öpG’Ï¬©*B9·7§(şšÕÑ^&]3£u?Òq<şR=…#Ñt¯0rkÇ¤•Ÿ¼ş_ÓÊ='’ÔÉĞ]‘À´?˜ç¼”†ÇL}jÆß.fEê…ÏÏÎ*?\G	×knŸ®V«Ã±“ÿ®˜Vø=Õaz9½j€CÈÔuõÔ1Ï¨PqoÌÜ¢kßÔtxßr?×óÔñ)³¤;OÑ£$;4û—­Œ¬üŸ® ¥s_Å[ú5Òq”ç*‘fÕYŞr±8ş©‰c5Ïvğ‰Ì°ü'.äí§©À°AfÖ~QVÖñÛûkDƒõì˜äÃ¶¤İ¥µ'’ê)g¡c(EjÔj”ÆêÓèÔJë¹…§Û¡:s°œø#®™#±ÊCÃ=œúhµ”@ëuŞ'ıéw{‰‰ÔÔ÷?;Cv»}ÈNú€AÖ7Kê† jrëúóã&-}5m- £,%hö*&N°ài¨Ò=A\û>¯A&÷ªVõ§ä§¥r‘g*kÈQ’ıF'sJ}ºüÍëÒ›]B¿XŸ&øc0w( æKÌiyX¼È«n}¼LMöSöihdÔÔ&”#4’–£±qôC¡#ÏL4Üˆ‚ìrèÓœWOhÓ‚ª{wŒW®ß4ZÔ©r¤¾îYíÃ÷Ÿ:»9zôW‡W.eO-Ûäs¢éˆ¡SWñ³ktxsGÚ0åOÅúæ(3sH HÕ“!İ"¶m[bƒ¹âÚ£z6D3×RN²·kw$Öj ?O_†a3!>ì®WVŒ´&>»i˜>ñ_2~Á$í, ,R¿i(³?Ãİù1¢øğc ­¥-şşŞ¹k‘ãukş±=$´6FŞ™»¶*u!‡£Óc2fF‡E|‡ëo5œ@ë¦ö&§™‹P/ f${×Îsö›ğ*¢8”ş¬ëZ0cj^Ö_‚èê6ŞÛÆæÓˆ’3¦s¹t˜Òİic™aVTüî¾ÔªJÿÙ’¯‚H/¿Ó Qå`É2ÅåÕ_Ú ~§ªªóZÙü8:d¨uüC`ëî¼#ø£Ã£bá€„7ä¤ìÙR#5å”s‹Éæt¼v;íg‰¬´ø˜œ9<†bØøV´$Í=â«t9ºiËî£HïTf$Ån½ÛeñÓ±îîwt¦+>vÖÍa+‡ÚÜ>£eî¶4#?LHæ¹EïŠ_°şÛØøÕNø…[IS—K8bofèúêS&”ë`ß†ø'P+wöu)_æÎÛå‹‰¯tH“1LJÀñé©Ğ÷Œ°äŸ¨Dfó¥Ó ’«¡rpº²vÖ	2â†œ~‡Y7·ªõäpé{Â¬÷úò\—Óï»©œ{N ‡‡ …#Dİâ@x ãÿµlôè¢(üñÇ9±Y—\~ˆ:¹;Ã-ÛÖzhö·‘¹3|
qkŞ«k`b{¾\‰œ"x¬q™ÛıÌA³N=Bx¨%ú««jØbC„„ùèÙOxç“{93¨#æ¾(1±@ ˆbb	£óàÖÉı	ÙôOO8Â[İc»j×HñÎáNs†Ê3)Àâí†ò­juÏHÜåü¿yÃ½(Ö5™xÖŒ³=ær{È¥Ï	Â]æ÷vÿë™¸	ß‰\Ş†Ó%¨§wÑßÉ´GAèú\QáÿÖ[N§¸º„ì	Áv‚é1´0CTm3h„ îk˜\yb5ñÃ'·šR1I®mm`8P€ëP-æ>bwmëÀ¹³¡Úª†ì°Î?[AbûîB©oÏRi»«¹5…ƒÒÂbû¾R¯#ú¨^k›ÔQ~|@€EŞ áLU>vÀAaÀ^ÕL>(•#XÎÁ¡7šM”}wéAæy’zìˆÃôJçpíöëª”ı8X°¿åj*§_êW@Cˆ›Hıí*¬4{ªËUE=BNîÑ¬‘Fæ2£VŞ&!r/s:Í³·¶ ş³=·@bùS`Æ‡n¤õŠ¯†îßÙû›ºâ›^g?¦ÁéùÃ-´¶4bÎÛEUÁÏ[ÅaWù|»s´¸˜Pû.¼Í%—Ò§JS^Î&Çñ“ˆûj!qŞ•­äÍÏÙ¹J%ú|¸ÍbÛÎm’…Ä Ãv’èoPi¿F è@Û/M{‰µe®µ	&ƒPH@;ˆ&.ß
ê Šäg;—Y·YQ‹Tm|ÿ¹
X^CÀİ¶­Œ³F†]:Ë^YÈWqÀªsÈ)·´ï3QIQØË—bß3†šØ:ò«¥·rvš´ÌL|_ñ6Tvwìÿôö¬?}û·“íÅµ[Õs´fï¢GèÑ«ä;P¨¸â1›MĞ˜a‰º'Ê"-£"“oáĞ`QESáÜD59C|ÿ„rí@RzÁ>{öA,b,]Ê˜ ˆ»`‚m "8ä’íXD¹,ÅèĞ_ÆÒH[fZÔh‚hÜ¤Í=Ì.ÜQ2«Â¯: '¯2+uñ'05£ç±*L°Xı1áî^%T“×&ô´Yag_•%½‹7¬÷íê†È©g`´;æÛiİ\ı_Zr'±àG‚¶ ‚R '®ƒÔ£LKb;OYI¾ò¤íóÛ8WŠûe”J.Õ•j3Àkl»ë¿GfåÔMİùÖUÏÌØ†RX¥¥Œ]’6996`ÂÈ`·Ğ`kP„`„-<:2·>ş<àõ¸¨Úò›‡áçaİY“Êj‡ay¶áİ:oKü«p¡óU·Ó··Nrrr×u¹—ÜêI’£@2ôÎ¿©™?*
É—½Éÿ
ÓîL:`­ÖPÈ|†A¢Û[ğg9óÌ=å©æáÖÂÛ/Y’å›+™Wq¹
äüŒÏEñ$¯ëoµrl»1+D•ó÷ŸQÜ;8©Š‹Úó¾„Œ~SXÈT~ƒs'O=ãyÛL:ÿiÍ­v–„Áîh‰…e †…NDÒù#àdí+5„Äk`líºS¿u°8—âªvqŸ•ç…+XrÏº]ğ2>1rA°\MÑ9fú?š‹Ø©F›½@ó/ÏsµÀ!­ıi|Ô]œÉøWg$ÍÅ€ô!¬˜¦hæAßg>³ê½şÔ¦¢­æ‹í¯~ÒàŸlUR¥-	¨
#9äl‡«†¾Ö¯ÈğhrmŸ‚ªo¯
#ß=Ho­Ô×ßd¥ßßÒ´ËTÏVÇ€¸²šõå?O7İßŠ¥énO×fÓœy¥ë@ö!Ñ¸¹İöÎCåœi…ª"-Á	óMJ@°ÛœQ0BÀkÃ[ÈcãÑË«È·Ûqñ×N˜ãñô†}M’àWìú‘D1'uÁŠ9¼ÖqÉQcéÕ²ï)íŒ¢Ü{“ê³lª“ïùL|Åäô¬îU~¤ä¿Û’%u&pÂg5ÛşrAçˆ÷¦}"×fy{' ¤ıÏ6³ù³ïşØ‡d•½˜ê˜ğÂßZ¿ÍCÑ§i«…¦âf‚qø!uOúÙYq‚¶©Üøªéó[HMŸë‹?Zêºø¦¶O‡dÆ…II%G	|²ws½=[Yxƒ/4¬{³t¤¥ï’Â“/Ö‘M©585sÖİ‘…&«q{ï^Ê`êšïFéÓ&4¶ğ¬Ù¹‚-(OQ¾–Z^(RÕMİ©¯&ÿ{áCØ ¨46[>øÏ·ÔÚGr^Ëu»Ä€<Ÿõ2Ÿ}µ™ÿ±ëŸ¯««£³Lx’ˆƒ©WÑR¬^ 8‘OÏ\_»ÖàÏ/©»#ˆ¸5vº˜¾Î{ÿ­©=mq¢¦”?Ï“ıUÌS¬¾üµT¥KG¼FÙM¼¼W@mEûŒ%7Sj&s+sŠ!F¨Ä1øï‹÷‹+XòmCw2T ¿óÌ¶±F(=×á°Òg2¼'n=îğ(5lÇXJ„óÊP4çŸÈ–(4z,[IÊKäçµõÈ—QÅ^m—Õ:¸pÓÕÕÜá…®¿'âï4qSÅ} I#°XB
óğ©ÁİŸ# ›lØo¼~FÚVÔ¶]¼h:Ï²BÏ ğ„.öÙ²JûÇÜırGşä^->E>ZKüÌÿÚå¸#fŠ™Ÿo³ŞŸ8÷FşÏPëÙM€Ã¦y§Ù3í×Ë×¥Ê|kšÂšñ³Ó[Y®Ú·òßlŠwè{UÌB®z®V¹G-Bğar‚™£3_¨'şsÌâùK‹¢×-§O%nOaQ³¶s›%Eÿı(ˆå†ÿ*¾‡Ó%ŞOò•¹5EwïÕÜä=ÿ9#ü²4r?ü’}ÕL—O©4§Ğ@\=[%‰ô®ï4z0gQ…?¾×!u"ˆts65/¯vR'îGS0Ÿ“2—AvşÀ<Nëœîí>…W	¼[Ş¢}Ow²…¥İí<Å½Ï9ÆvEF'T60˜Ñ‰VMzy£X~¦³"Á™ŞÆ¼È¸|î v¬š½Ï´]ÙÒ¢fz:Ï©òwı±}›:¹'µö0à»¼6€ZÍ÷¨]OÙû³Õáª / õíƒÒ¹XXpN<N“zÙ¿îŸ[Y•.Ü5|¨OÈiÒqÎ~~Ölè¢nlÍXs}}II[ÉCkI}	ioIŸ `¶‹eV w0Ë±;P½6D9;•:½”;¾”jcôzOÆÚß¹»…LÈ5Hc¦F\v<{‘u ‰³Vô.9ì§ôåì¸kD²¹	)f;/Iåè‹%ÿÎ­mŒßù¸ùØƒ#¹¹•b3ò=SS14¥5=z²Âµe_‡ár4¢?îoëÛw÷õßÚ2ËÀFRÇÆà¹ÃuŞÔv…„4UßVö7_ÊB#V=;Ä(ãî»Ï¸†œMÏãÏ|gg+gõ~ÿt™Fmœ~[Ãußì=Ê¶Ò)Û^r\˜”­(6°»yéÕc§…0·4)Œ¾óB¡õ>üT–ô,QÕûûxıµ”‘n;?aô~ôãk¯]6Ç@ªNFI5ƒ%‡Ê­ñ6­04!$¸'*¯±cÜfÒÊ±ZëX&C&tTÛÆt''ìÏÑ$d±´òX	Âä2dË³2«‚S¦P5{6åÓú‰“v:Ş¬‘é7¿İ´æè™–vƒ/gõ&hmå'™aå>Â²ò[ûÊ³åOü$Œû4Ğç³
9d¤Œq¬Èq¬TÇ:ŠDÆvëò«¬€p™Z	‡`	“`‹p‰¤$‰¤,ç¤,‰ü$hrØÃ)»İ);æß(Å†{ŞyÂÆİAWë—Lƒ'à¶b²Z?júÖrSIY&bÎã€6Õqµ/r`5ez³Xâôœ2?†ÌşMH¿î—™z´WLöì\«ÕaÙÛSaaæŒdp¬–_èVÉÁ!r3›¡ÈxË‘Èm9„9^zÿîÿ~9KúÕvOsÑëMA·ñ[‰.{ª7cXµ”Qğ‚Û¬ZA^Ú’Vpºö:—òQ¤JÇJÙw'3½™P°ŠC‡»×Õˆ•a+Ÿª©ª¨¦<÷éQœË›n‰‹ü—	ÙğŸ3 ãRzH{Y^¤”«kJRÒçÂxi™×•T—u;´7™RórHCı€Q‹@Q·vM˜ˆØOĞWÎo–‚(š¯¼7uhWØ¹7NäÙ×pá¼¾yE9@J™ÈáàÒºr\À^ğÛ»ÿnãÕİüf'{«ŞNHm5lYŸ!tS#Œc¼¯ûrN@³»à>¸_‘ğ
RÀÈÿ©9# ¥|ë+Ä\ÛÕ^ŞÌÈÀ6ëôŞÏòQ\d&¬B¢†ÖşÑşwh¢L5ÉdpèÆ*ÕX®À“eBkÏˆ[sSÅq_›İ€†Û¾8îÉx¦ú@Åÿ'Rä OwÂ¢}É©İô(go--µÁQö‡%eoÇSƒ_ˆM\+¸¿!w^&¿Yo'ÛàmŞ|¹ªÇp«Hi!ô°ÏAİuÀ‡º~¢+aÎğcÆŸcüRU³\| §™ Ğ<:Ë”Å
 ÌjhÕéUzm™ Ç_RYõ•­Ûõ†®%¯¢­ÅW!ıÊ¾~§Ñm9	ÌÔÏQ@^¼³öÚ¥Âee ˆëıüƒ”²ÄÀÜêà?EÒ ÍZ %’!¤q§¯‰3l¾ÂÜ"‘6‰w&5!Oç­•­Cˆ˜İ¾-Îr1±0n›¿‹Iÿşïäg(ÆEïW4±W4,µŞBÊˆÍw•gúMD–½ÒXÆĞ å/!ëŒw·óİÂ²X“RîN£íåæµ¢ç÷ÒÈ$ uÆQ…ê/îùga7O”|ÒûÍ·}}O¦:¶ò)BŞk×ğLİã­SˆAÂ>83‹<;í;‹ÿ8À/Ü¦ƒ¨pÕ_Tw—Õà—è­9i–Ùwõ¾o‰zË¬ı¯æS•+8ÎDíoè,zİİo:Ép½#¥©Ğu¾òÙØõ?ˆ7Cçšîcâ®Ç¯×CÕ•”^°vøµ­ŸÂD‚233¶ÆbK¢r§iºg«+ñm‚Svè:f)|‡Œ{4Ó#QÖQc'\Œ\,‰>Ğ¬{¦ö|`í‘5ÁüåOï!î¥Ê%š­E³FfZãEy<zÙGÑ¹#.ónñİÁÜ?°ÚïºòÅÊ<Ÿ4É9Eş®=ş¸ägø®}ö¤A†>Ôæí­Ğ•D!¨=eøÚ,ã>ÍÍí.¦9<Ê©áÓëµT”MT&~¿ĞX	ú¡¦J’ŒµŒÆñ°§Ğ"é,kƒªµÌíáY‰'J‰r5ùX·å)—h™ÜYÿ²ÄÆ[e‘YÉ¾îŒ|‡Bëô£‘yjS×1-Ø?iâ¡Åã]qbf×+×“+¼(÷(ñÑ¼8—bPQv¨/†‚şyÖ+6>=Í.©çµÒdR»’íÓ#Ó§4RR¸úPÕSOøÆ)ÒjğÂ.Ÿ5ğğYjt–ÄòPP(SFéÀR=ì·x€ØyÆ÷­`X_0¯ÓEØ-¡Ô>¸	cŞ·]7€İÑù‡“¸LçŸ'Wù¸2éOgÚÈ6MpˆÕÈrËÕjr!¥#ªKÍîë›o˜xÊÖôFèà˜~4m[±‹¡3?j[¡TïVMØz}ˆ`uqh‰Rúµ¨¤¨ÄÅyéŞg047Ê«ãò’©ë2]4¨–,æÆ.dK|¼mÇÖğş½Ä¦·J>R\Ÿ­Sö¹ÅÑÒ¿jšê0ábŸ!J£üvÚŠ³J›÷tDC°ò7öX7pÉ×½Ú¿ëìÿŠ·y¡ˆH˜ıİÉ!nREâ;Wb–2ãã[4“½ZP…7¥e4=µ7Ál)ƒÁ¤¤^­(`.
Nì¼İA¹±¹y3HÏ¨"DáiìjDå4‡q%;µÌ{Ó¢·’Ôh^(á„55×$¡ıåìÆs­Å4˜>--Ğ[	ÙÀ¼_´Da!-¥;=ˆƒ¡¶¶Â2¯;3P,Û»ğpº¨ñJ­t¤}î,G„]ïÿ(‹ËªñÁ I¸€
4b¤94ƒ…4MÈbååÇh{†­àhàbıRV‡ğhÖ¶jö¸PA„V8à¤F{†s"1x˜ ^È\`/ìÍ‰¾$’âí]d®U­¢†>Ø}eéHyÑŠ„—o>±»XA“ñ\c»'T_]u[°°«±ôä«&'ÔRã«D¸[s³Xá$YÈÖú)N9éØÖ Œ±=N[›³Íš±œ^Ğ£ÁBÄqnP¯Ï¼§g‚9 ÜA}° –ô–w^?xG~sÂËw­7–Ñ»ö“_»²gÍ¦[{V 0f‡Ï<«¨X[~ko<é°1ä+§ò¦:7°¿;E/@t¼õîÛŸ0³?Ö'”Œÿ•Ñ9ÒúÖ·®ëéûüp0ÿOX mÏ¥¾7‹¤ŠøòÚÁ.@o[‡gC:ï» ›:k:?ûòÜ£Å™øØºu_ŒÇ7n[;j¶ì‡t®?³ü;ñAG2ü>Ô‹ÆøB³& -?yíf=Q5…²¦"dÙÁ¸Ø¸Ëèb6“rÂ—nƒPb6µÎ­x›[8î“‹Œ»´‡Oˆ}RáB•ªŠïQ™&-Ã£ëª‡ê¿ê¬(µ$T>
xâÃÇhó¢7üôn+};‘Ëv±š·’ÎJë¥Hex:*öuöPŸÙËPîD|J|‘Şû½í|é26óiåºö“ÅñƒÊÜĞì"ÿfæÏP|Âù‰$xn8†ÔeøùÍÖ[İ\‰áBÜËXnŠ-–ŒÍı,é^M¹‹¤š.	ß¢ªÎL²p¡”ğqiß°îJïˆ]³Ü^¼­ŒwÅÙ#s†ö<wZ„+Ûñş*,+D>/\}´ÀÔiálµ—ú@Ô=q¿)t¡˜ˆ7š
‹³èÔŠâS³µÃx„ÏÚ¨ãdtä	ì8üêšEp÷w.H()HæAÄSş ìñp½\âÅoµ4€ÇWR¡¢òø@ñ¿ıü×ØÚÔˆí|ıDdôT´¹n,è¬À“‰Xõ6/D$±»Âwé¥:Ö*ñÙ‰ì©ï0< ¡A÷—¤hå~Ò—M0ÈHb{Y‹‡‹Ï5ıyÃé¨™{j1eúY¨jlxtªñùæ
¦¬à«ç¬JKTØ¢N=”,Ú/	uå‚;Ÿâ±Ãä°ìµÈãø÷ã}(â­»ÉŒWmüoLŠ}¿í1‚ôZ—„yB5läbÙ
ğÍäE(aÙ›Ò¼HKvu¹"h÷\7G><ÂYm‘E+×_,®¯ÖFÛšUq>Íÿ@$¡õÔû5jš.©CÊEaÇ»ï)ilÅH¬ëR[2»è(^'.ÔÆ[V‚GV˜Ë!]diOE|[";J	?xÆÅ©òH–kx•YŸ„R²¹²ú&Åi4šyÙ•æK‚Î6§(ª¨°˜Ûó3!B³Ä¡§bRnçÎgÒ	ñ-Ï´‹Ë¤	($ï"ŞàË5ÅHˆmÄF{XĞÉãg÷Ãê^íÇÍ'-´¹cÌ#B(ãÈ‚ò•JT·—7Ó
5Z¡È¦ónÍ¸õè²1Œpóy+t7å03vï}ü×Úƒ7ëLb_|ú’»6.cíÂ°êÕw—T8ó>Kƒğë=½‡¶Ês€Í}ülÍóÜ 
1Šiw¦¾pt`kèŒ¶æ÷Ëõ¢,‹ab¿5rÈ~‡Lñéˆ€1ET©Åæ«v34/a¬¨&*Ndë7ƒlKOÜ¹Ìi4·1ñq¼İÚ°´}ˆ°(pN-ÙÊTnY¿3¸çM8\×jA¼˜Ên<–y#Ô •ˆ#\˜8Ôªƒ){%P/œò‚7Ü¤urÇíF:¦ğ­Ğ£w~·ôÁ…•á2ÃVøAO™3%oCtîº¶5à~xœ.ŒŞzB€ùÍ7|W^½ë¢'…ä°ÅÛw¥`Ğ¾,³tß­[§0|
Ï1§.ÅäËÓ‡áU"üM
. IZ2$^*|L'?[„½ÂQÚí(1Åƒï¹Ñ‹#`	£PÉPHKØRá©.¶1¼7»m*ürøa!ö;ìW¹"wcPä¬°‡Q:hf¬L”Ëoİ|Æ —îCk'BN¹MÌÊCÛıæ|4ô9âŞÃp&2àkÂ‘ñJÔÔR&98Ïtáœ6û¡/¨!’uªñT6ÇŸ}zåUdrK0Š­Å×+ïè$ıfH®4ÇŒ")s10‰â§4NCzÈ2´¹P]†à‰Oh­êá3vïÍnh£P½FÇfA®	iA|àgü³.Û)%İÒ:²ëaègofíe9gbo[Š„ı‹ÖO‰Ñ 
9YÇÔşÒŸŞaó„°dèã~]¡ÊaMBÙ/ºÙ«ğ¡,L^ÒÆØœä±H.‰Î	ï{]CdK3‹â[ñ_±dßå¥Œ[ò¿	WBx+¶r»´¼u—jÍw0Á' ¹ËRr[‘Q¡2Wê–²c0ª™6¥“0ª\‹ê†à¸}#aÜÅpFuˆ‘”·ëhùª²}(ábŸpªò ·š–BôPL}éœúUŸènAJœÏ¦ãW[ÃÇ(”^Ç£m˜J[­¹]è©¹^É¤A lÿ;ZOeÆ?bÜRÀFsıJ)"‰Î‚b…d&¬{ã‰3#hy–Ù ğà!«SmÅ-Éwxê¼-8Ä™á~­"^:hF@blï& 8ò®«ÄÀ—4¤šnÄÓÒ÷Š&Ş·Í¬R=‘	z,‰Ğ”¬ùğø+*=7E{?5
aIZÂdx7ÎğÖù*<H¥Á{7»Ağ8¦pXáG®çÌÉÍ—«”ğ[‡‹1a÷>Rwí hzßJ%4 öt¤)WÀ2|"úW„(ÒÆ+÷À‡ªª‹«4"6šÂj…ÙÿLùaPLp’Ìıí¹af*Äb=7ë;Z~ØcOÅÈºMG)IbR’ËÀ×¿`RŸˆ‰ó†S õ.RÙKß|frddõ:¢‹»‹	Ù™J¤üxZ’»í©-	å&8hÚ¸£­f¸TvéõĞm‹R2Çô–â˜x‡l[ó=¹í°Ï‡(šCØU‚­c2$õ˜äDmVĞæƒ>!³ÊîÉjÕS²·´R)Œtİí×ölA`$BK¹õTFwUgõÆ3íÏûd²óÕğó$d¹—iaÈÖˆ±•%Ñ
GïEN‘pøÛu†àaQà¶?z³'t…ÈÚ5VQ‰Ğìv¹¤%^v°ä\CL18EÍ´/ŸÒ&½üÃÆùTí¶t>Ô@ïñöó×Ã+{iÀ<mˆXSÉ¤ 4úÿ²®¶-Iz €CK¥ıS˜pÁ–-ˆfæŸ¶mÛ¶mÛ¶ñ§mÛ¶mÛ¶mÛwïÚUıuí¯OŸ>u×z‹ˆ13ÆœùÄÙ™0ŸB2$ç¡ Ö•İ=ˆ hˆÆW:/}„ÿÁzrÌëä”Æ²/ Sï®„—ñ­†÷ªÍõ	ÃüİÂTc,.’çˆ{¬›”Ya–ÆHBè|U®w ä²C”ÍÒ;ØóHØIÜ ¢4Ş'Vn!éj8·õÂjèÃµç§‰q.¾€ïÛ´÷ií”işß_ÜÆ‚©…~ şEP§ú_ôóKük†Ë¿E˜ÈMÊÒÃc·C7Sø P´/áõ±ˆT(CÙÚªP%
·.éöİ)„ÉHxŞ]ÍÄˆ5æêİâ²#´QÛĞR0u8ë)7¬8²Ò8êÈKÒ6v<ÃàP=—)ˆĞÚ¥PMxùÕôéÊú¼Ûc’=òõhé©éş¶mWSY095VÚÌlïöÅ-obwP§Ùÿ†…¡´NT¨=g÷³Š_7Ç—Ï`¦7XÁ"Y¸İ•<My°K²Éâ‚€F6´`èX	rµÊa6öğƒñ0£¼¼™?ä×{C³ºÏÍ„^c´õ#Œ\áÛÙş[/ğ¼”@ığ?•xÿ±4ÿ«mü¿ªùoÂ!6±–@ğ"¾å•Æ§0ÌXŸ (èâyrs¨ÇkBL<I<T·Çä	˜ˆCÔ#äk}º}µk RÒàO¤ˆ°½œàSçÒV2£QJ»Z±±4¸dä0Âî”Û™ú—‹=¶U(ãî¦(iÇ§a÷»Èm‡—gêÓDMÈ(]B3[ñZ²uÔl?:¾ršÆÁ²¼ª²sRôöW÷ùò–FT5VxLİx5_~ü>
ösâ\¼ıFåşû~Ô20ısÂà¿FùüïöCŞÀÌä¿6C
r€YävÑò[:«íä5(%x@pÀ#ºîbRBˆºNÖ´½‚!T‘,í™£Ÿ›ØKş Å¿4ê(#ÙêÆÚì¬†™uO”RÊçì)BğI“‘ÈÁMšW±•”Â™ô¿/§åÊÔY§I<›˜­÷™z8‚úUåà“ç7vÏ–FÃdÇÑ•’½kŞ'°*ÑÿÄ%™ÕºA’Fù‚‰E—J™ äkåûÁ¬Â4·Ğ»ı÷åcDvbüs6`ŞŸ‰3ÿ?-_ÑÎÎùÿZûMı‹V´±B€÷aåñ÷`eYşĞ­–ÉL¬ı&U†AIÂ7$Æû°ñÄH¤Yí´ï\=˜ElŠÀûü!wpEªHÃ¦æbâzœbšİzÅÓë\ëoéM#Š@bYA&ÇØ1N?^OÓ$X~õ÷¹ÿ˜v–,ŸWˆEŒˆX(ıÃÁšô©¯ÎLT|Ù	½›ŞK'Ş-7ÑşT/™ìAX·œUØ²ŒvÇ¹îŒšQíÆHvÒ4)©Xæª"Y‘H4±;QädµM"¡w2ñ±˜g&e=c«µk´ efIOÌ”
tô[<ïÿŒsşœ.ãU™d‚èªˆ;U<ª‡LJíòTEO*õ*´5TÀf¯¥ıÛÎ{Ô+jü!{4oÕ†·‡‹[G¦ø¸ğÉHQÆ”p>¹ÏwM‰k8ÈZ®ÚÊPÊ$@Gõ–CÕZõx=©4^¤[Dqœ-l)ƒÙÙeûkæoÒéÜã½V;Tù	¨»ÖŸ÷¡¶±™+ÿ½ç˜b3Õ=EhŸ\g‡Um0¼¯;øL11I!Yı âÙ³7Ã±2[ÿN†É°ÀÚÂYV a3B•n©iŞI¹ÿğöMöß‘AˆìæûOmô	¿şo‘ñ/\$«C¸+/£üÜ“&JãÂ#şş´EDID$FäÏ°ñJ÷ †#e:ìJÅoÛ·¥©Ş.’¬, t®WI’jZÙP­¬ŞX¾ñ\^ñ¾.øõL”`Ä òñšÚœùØıÍr²ËùÚÛ˜RID.N´! ÂfAJA°d+—/ÅĞDŠİ”Ùw`×²cú’‡t°Ù(–'´°fNj53³Õ	æNo¿beÆøË¸ÅÛ×$×o$Œc7E¡‡gÅd$šmœh:C†FabÎ†ñ"Ù=(On±Uäv_†kÅ².Á´BÊˆÓa‚5N’İ‚K!c¼µaÚ’Ï:Ø"XÌYø·AÇJÛšJš· Í«Ô^ °½ˆ¨¤¸U4Z<±z3¤XşddÑ"s~qÛ²õ˜ÊåAÅ¨Ä»CP×cø/ˆ·¡9¹€€nEÒ‰ü£‘5À¡|I>z!Ô~¿{°B4°±‰™Xe¼6c ?w•Øê¥„RÎ"]·kç¸¹éQËÜ#kmP‰½å;í€üÕŠÕ]!¿aVM><`{¦„û€D`ÛÒˆQB#ÆÜÂQ€Ö­øÍ"+ª
4Rcâ=¹í¾ßu:jc¯º¸Vƒ2ª¹˜ˆ_¿êBQŸş…‚Vâv™›(É^ÆC¬¹B^™`óZV¦Š.Gãó!ğÂ¯º¾ü"I„>îÉ)=Îrë×õ\ºÓÂÙÖ‡ƒ! T¿º™˜÷Æ:‚‘d…Àà8{q\.jÂ{ŠAçAÑdü ½HÖ•ì°"P;·Ç[ÇÃá! õˆŒÈ\B3d££Â1O¦¦:*‡Å»B-½¼œèJSS°‘T½tÿ,ğ–!5ÕìôTcö¹r#´©KGõg=£ŸJÀÚØ‹ûÂ–hÇù|Ilh%H½Šz!=a`Z™q»ÎrÇ8x'{õ0§Ã¤e®³±òºË;›ŸéTjî³¿¹8Â‡CO@¬(æO,Ñm¨qÍ>’9Å H}¥7œ@÷$s‹z~‰³Şsa2NRŠ»[åÔL°5m*:`P½BşQr4Şµ1E6møha.×€¡í\Df¤>!eb_]fù>kñ¤ç$øÃÈ3ğº×Ò›SYga«DëÛ µ3¢âø›-3„òT^ñE>ˆ±³Ä)‚+´mÿ0"5-†ôzhfˆ”v‚å¾',[êW¡ßµ¢5½bÄ5¥?ñ6S‚ªŞ¤aiÈvOUíşßÔ9¶0T¶¦Ñ„ìXîHv§s_H® Á\UrÂg„EiL[Ä¤ÖÃF! Ôù²ıGÛ=½9ú	˜ÓÛNƒ–æÍšGºö»ı='± £|_Ù)Ş~Ñ(½ÕOÒ9Ö6j04‘Z–­.à‡í€sÛq¶Èn&‰X
Y4©A\‰Ù@OöV$NK³Ø…-@„½º&èE!Ÿ¦ÚjXœ…Šò¢‹Å¤ä.c*ugz“4ÛéÁyÂV‹šÕÂlìXË?Z!ó,ûìt$€-7!’¨¿ÍÑb>Òªšëçç®	
‚iW5ó¥÷˜Ù'B3×½ïÌÙÃBA¾tEGìš¯M™ÿlÑ¤¨e¹-å×îs¹“Îº­?ÉTöØ-ßñ\ÅÉ,Awú6/êÅâ½k¸~³O›ÏJ°ìCÔ¿±ûÁíS¨
	¢>agóŠİÎŠt–ŞG½­ø#+æÖšºÇà¢+©âÌìÈİ3Õ¿:µô«²i˜Á°|)ğé!>ÀX±>=N‘ì˜“ÒˆJïpu´§q(ÓéŸëœWÓCu:Œı•7FVÉ ½Ş“Úí_”É'ŒTÄ?ßüè	2ñ2á¬	ñ'¼'\å0 fIÄ©dEB¡î`9Bga~'û!Y<A)X?±ebî
A=ÇªíDÍR\¼#M‘*ôŠœ¢ûpêê=ôù›ê_ÒıjGgR]šËò¥%,ó%6øy3¾ç‹	Û–üJ°İüÜ~.-Úù ŸM´	ú©ÌûêÉÛ)Â®SK´õ—ä„Ú½wu/[H,û\¿˜¬•vA92Ìa²©˜dº %ˆ%@‡%5B/²ÌÀ¨ObgZ4™}¯\1aÌ‘í,?¶-İtæ/û”Ì¾´mÂe¬…4Ş F­…Õ”-›5Ó”{æAöx]ı’³¬b ôt²t» ƒëãF™­FÈqöHò]@ÙC®X7·˜çQß¡vû´KMVßŒ‹òÇÄ!V-Qùè<ßØÃEÄ£{Xòš«ş´	İ(«:,¤.L$œwîÚ¢^$Ò{	'z%÷ãyÔÅ…U%©£HÊSg#Q6S“ÌŠ`8èæ–e¹Ì.	1·„¸PS³C÷U/ÜØ/¨ggH'EğgCİÇ¬.\/k:Tú—DUÕÏú²
Î`¦´™ƒ“Šƒrõ…è‰?e 0ò($³ºg0)6‰ñvgÎEœœPllÔÏ&!>„÷¸ï%Î1ÀúQ<ÀmÀæ÷û¼äSrD¡*ÓÕ]‹ëªwsøÎ|R‹í9„6v´Y: xÚ°Ô"3ç=¼Úd¹£G¶ßˆ£µivl“°V7Íß´SÚé²Î>µmz±6~Gfü_,,Où¼v:*½\·U‹í7.ş¨7Ñ,›€ ++JíÏˆÕK&ËÁRSl±¼%glÿÑıKts™İ®ëéY×zñ- ëT
Y‚m3à^ED³bD:# 3sœ^›Ş´îwÌ{šñ8?²2óG4Ê-»^+ÿPS×-…í .TçŠ÷fÙ³.½¥ĞÇİÁW4«šÑÎ>~ât§=Jkú¤¤TÇ¢¥¬Ñ¾^sšçT»ÀY»„uß1¹åBÑZmõ@Ò©µ¥u³9”kãÓ>ûbøù‚ŞVYÎ¡•rˆw—Ä¯Ş£N/y£ğhÕÌWÌ^o”Màùt+Ò®aDSz×pŠ÷J>Òï¥qàV)ñ`÷Cî ùà09$«¦}ÑbrĞÊç8÷é)U«ÏÃ“ÊæÚ‘óPÒD’&{Gw]µ½%­{öœT­êçÅx°ê ø–îî„æî:PWç¤R#Ï9¬íısİ ­$R¾-4#:„Ìó®ÙÇQ–.øô5à;k€w8?ø‡ñ–6
âì;Tù©ôn€×}¼ç´ö¾œQï™ÈÍÇİÓW*»Çë°#›Ç†í±-ñövÀy+šCû°½-ôj-ÒDØ¥Ş† á #=m£Ë6ÿ+yH^ıĞ4µòk£?`s¡’1@úÆgRC¨Éw´Vx¼aÉÏ»fïNÌpuÀ{6wŒ˜¯÷7XîF3È=ùäØ¨£ñ#<û¶ËËœÿñ©Xèî öMöc}"öXù‡«ı†rµ(j+Ùó|‡úÛôÍ˜óuãûr11·s²øg¨Ò#îwªÇ§ï}VÒ÷çÿ»kÑÃ'¶±|÷İU‡ÁwòR8€+ğn†¬©9¨¯ìCZñÖld‘S3ŞN)¤eçw¦s_—uî£]QĞÅ¾T%mG·`<¬--Áu?¾Pec¸q†„vl}[NÊÌ÷cHÂ-O—T²0wiŒ×É–dË1÷|™2vZå™MëØ-çóO•CÈ#•ó;%ƒG0±g´@·¬ô~ú.1­İ>q×´ê+,æ=íæ˜§,P²mÆî|¢§hÖyR}¨ŞŒ<H×d¡1JÅâ¹Íi£ôš$v4—c±h#¤´djğÏ€€‹6îœ.Wr¨àqğˆ›³Ò:½Ùƒfş×Ÿ7ÒI•’‘97
µO‚”ĞÑËäÅú8Ùh•Ã=1YÃL¿[—u/8“b#%÷S· ÇÎŒ€ã4òw¾ÿÃ?9^¾ |Cş¯k&ÿ5ÈQÚÀÅÖÈ\EQú?óxU5,tÛ<è yzpËê„…”:èjcVÀ‰ƒYÒÔñ9ÒìàbÚßşØg¸7ŒXÅŠS÷‹ÔœB¾‰CÏÛ $”§ÖWïÓÍi¾ßŸ2?Ÿ¯>ûe˜äË‡6Í#U	i–[N-\·å*Ôµ¿¼•~MUV™–-ù3'«í7Teg\Ğüé(£Şi§E­† fVÒ¯2£9G¨lÌ¹TìnÒ"nÚ5ş×™XÚÓE­í¸oš;ç6¹›¸å”òè–@«†afzŠêw:‹¬!¢Ò›O|DéÆ¯ŒB×AJ{Ì„¨"43LÌf2™³ÀX7´ú€‡gÍUí%ê©lVßçÚ“9ãÆjiºã÷Aø}ö$é<ÃhƒúÛ)YLÚœ*YùÓ‡>!s2ÓŒxV‰µUØÍ.KáH1İ¦˜şTÇ‹¨»{-|^.ë‡÷IOç6Ù©A¤…—É¢¤æ
ñÊöñù* =å‹jIÊw><˜äÌ d`.$•iÜí(ƒ$Î¢óş“b´ÆÕ{–Ğ0a¬øVGÒ‘›!zIı³CsÑÊÈ:gg¾<¯ägFa“¹SGrñ
µ^4^Ç«Ñ•¡™YtÈ8;†LÑİ·ïâ.“Ú.o§Ì±Ë!çîò'Q“oæ±Õõk,¶–¤Õ]ÊFác¦ÒZT",¡‰Ş”ßb -ö„!ÒZfu´)ƒéqc8¦W o(ß²òçáË/Ş3ª‘¾3Ço“!¡Û±‘¢ò%Ü8X¢‚ÂF,Q†şäIkÄà—ØKñK+ùgSÖìAM¸+Œ¥´™xOq	èÅDjÙ…ä¿½»×µæ''ˆ³wà¶ÑÒ·Dd©’Ø&šXC“\0Ğa*ÓFp©™“pÒĞ?åPì:9Ï›TuBWõâVB?÷ EĞw¥We”ëí[å?À¹5³ª…Ï2ÛP›œTõÑ—·áÁ7£[¬ºÃ—SRÃ]S£7¼o>ÌÎÉ»)1¤æˆQìQ¨Ê*Z4¾¾ûó‘G°é3>}Ñ%]ñ'\"N#sRŠ©ßÈ*Ò¹D¡³
P¯eK5ğ--H26D sÊÕ2ÿ… kÿû.©PÈh1©Z/cFƒË“†è^ n–…íş	}¬+b¸x¤µõ¼45 X`	ƒòGYÓL©PRÃHíT©Ä~V­`‰î†_†KÎØeÉ=z©è\Ngúœ¦Ä©Z³œ`_û‚"ÃÍQ@JCüÏ#°§^À¡iA<][á&ŞêGíë,°’