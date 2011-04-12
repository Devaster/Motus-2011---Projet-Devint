package tournament;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import static tournament.Main.*;

public final class Xml {

	public static Element refRoot = new Element("referees");
	public static Document refDoc = new Document(refRoot);

	public static Element playerRoot = new Element("players");
	public static Document playerDoc = new Document(playerRoot);

	public static Element teamRoot = new Element("teams");
	public static Document teamDoc = new Document(teamRoot);

	public static Element competRoot = new Element("competitions");
	public static Document competDoc = new Document(competRoot);

	/**
	 * Sauvegarde le fichier 'referees.xml'
	 * 
	 * @param fichier
	 * @return true si bien enregistré
	 */
	private static boolean recordReferees(String fichier) {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			// On crée une instance de FileOutputStream
			// avec en argument le nom du fichier pour effectuer la
			// sérialisation.
			sortie.output(refDoc, new FileOutputStream(fichier));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Ecrit dans l'arborescence JDOM des arbitres
	 * 
	 * @param refList
	 * @return true si l'écriture s'est bien déroulée
	 */
	private static boolean writeRefsToXmlFile(List<Arbitre> refList) {
		// Pour chaque arbitre
		for (int i = 0; i < refList.size(); i++) {
			// On cree un nouvel element referee
			Element Jref = new Element("referee");
			// On l ajoute en dessous de la racine
			refRoot.addContent(Jref);
			// On cree un autre element name
			Element Jname = new Element("name");
			// On met le nom de l arbitre
			Jname.setText(refList.get(i).getNom());
			// On ajoute l element nom avec le texte sous le noeud referee
			Jref.addContent(Jname);
			// Ainsi de suite
			Element Jskill = new Element("skill");
			// On met la competence de l arbitre
			Jname.setText(refList.get(i).getSkill());
			// On ajoute l element skill avec le texte sous le noeud referee
			Jref.addContent(Jskill);
		}
		return true;
	}

	/**
	 * Affiche la structure JDOM des arbitres sur la sortie standard
	 * 
	 * @return true si bien passé
	 */
	public static boolean displayReferees() {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(refDoc, System.out);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Lecture des arbitres depuis l'arborescence JDOM
	 * 
	 * @param myFile
	 * @return la liste des arbitres
	 */
	public static List<Arbitre> readRefsFromXmlFile(String myFile) {
		SAXBuilder sxb = new SAXBuilder();
		List<Arbitre> refList = new ArrayList<Arbitre>();
		try {
			refDoc = sxb.build(new File(dir + "referees.xml"));
			refRoot = refDoc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> elemList = refRoot.getChildren("referee");
			Iterator<Element> it = elemList.iterator();
			while (it.hasNext()) {
				Element currentRef = it.next();
				String refName = currentRef.getChild("name").getText();
				String refSkill = currentRef.getChild("skill").getText();
				Arbitre ref = new Arbitre(refName, refSkill);
				refList.add(ref);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return refList;
	}

	/**
	 * 
	 * @param fichier
	 * @return
	 */
	private static boolean recordPlayers(String fichier) {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			// On crée une instance de FileOutputStream
			// avec en argument le nom du fichier pour effectuer la
			// sérialisation.
			sortie.output(playerDoc, new FileOutputStream(fichier));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean writePlayersToXmlFile(List<Joueur> playerList) {
		// Pour chaque joueur
		for (int i = 0; i < playerList.size(); i++) {
			// On cree un nouvel element player
			Element Jplayer = new Element("player");
			// On l ajoute en dessous de la racine
			playerRoot.addContent(Jplayer);
			// On cree un autre element name
			Element Jname = new Element("name");
			// On met le nom de l arbitre
			Jname.setText(playerList.get(i).getNom());
			// On ajoute l element nom avec le texte sous le noeud player
			Jplayer.addContent(Jname);
			// Ainsi de suite
			Element Jskills = new Element("skills");
			Jplayer.addContent(Jskills);
			List<String> skillList = playerList.get(i).getSkills();
			for (int j = 0; j < skillList.size(); j++) {
				Element Jskill = new Element("skill");
				// On met la competence du joueur
				Jskill.setText(skillList.get(j).toString());
				Jskills.addContent(Jskill);
			}
		}
		return true;
	}

	/**
	 * Display all the players
	 * 
	 * @return boolean
	 */
	public static boolean displayPlayers() {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(playerDoc, System.out);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Retrieve all the players from JDOM
	 * 
	 * @param myFile
	 * @return
	 */
	public static List<Joueur> readPlayersFromXmlFile(String myFile) {
		SAXBuilder sxb = new SAXBuilder();
		List<Joueur> pList = new ArrayList<Joueur>();
		try {
			playerDoc = sxb.build(new File(dir + "players.xml"));
			playerRoot = playerDoc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> elemList = playerRoot.getChildren("player");
			Iterator<Element> it = elemList.iterator();
			while (it.hasNext()) {
				Element currentPlayer = it.next();
				String name = currentPlayer.getChild("name").getText();
				List<String> skills = new ArrayList<String>();
				Element pSkills = currentPlayer.getChild("skills");
				@SuppressWarnings("unchecked")
				List<Element> elList = pSkills.getChildren("skill");
				Iterator<Element> i = elList.iterator();
				while (i.hasNext()) {
					Element curSkill = i.next();
					String skill = curSkill.getChild("skill").getText();
					skills.add(skill);
				}
				Joueur player = new Joueur(name, skills);
				pList.add(player);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return pList;
	}

	/**
	 * Sauvegarde l'arborescence JDOM des équipes dans le fichier de sortie
	 * 'teams.xml'
	 * 
	 * @param fichier
	 * @return
	 */
	private static boolean recordTeams(String fichier) {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			// On crée une instance de FileOutputStream
			// avec en argument le nom du fichier pour effectuer la
			// sérialisation.
			sortie.output(teamDoc, new FileOutputStream(fichier));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * methode statique qui ajoute toutes les informations d une liste d equipes
	 * dans l arborescence xml du document jdom en dessous du noeud racine
	 * equipes
	 * 
	 * @param teamList
	 * @return boolean
	 */
	private static boolean writeTeamsToXmlFile(List<Equipe> teamList) {
		// Pour chaque equipe
		for (int i = 0; i < teamList.size(); i++) {
			Equipe team = teamList.get(i);
			// On cree un nouvel element equipe
			Element equipe = new Element("equipe");
			// On l ajoute en dessous de la racine
			teamRoot.addContent(equipe);
			Element id = new Element("id");
			id.setText(String.valueOf(team.getTeamID()));
			equipe.addContent(id);
			// On cree un autre element
			Element name = new Element("name");
			// On met le nom de l equipe
			name.setText(team.getNom());
			// On ajoute l element nom avec le texte sous le noeud equipe
			equipe.addContent(name);
			// Ainsi de suite
			Element selection = new Element("selection");
			equipe.addContent(selection);
			List<String> selList = team.getSelectionOfEpreuves();
			for (int j = 0; j < selList.size(); j++) {
				String compet = selList.get(j);
				Element competition = new Element("competition");
				competition.setText(compet);
				selection.addContent(competition);
			}
			Element participants = new Element("participants");
			equipe.addContent(participants);
			List<Joueur> pList = team.getMembres();
			// Pour chaque joueur
			for (int j = 0; j < pList.size(); j++) {
				Joueur player = pList.get(j);
				Element participant = new Element("participant");
				participants.addContent(participant);
				Element nom = new Element("name");
				nom.setText(player.getNom());
				participant.addContent(nom);
				Element Jskills = new Element("skills");
				participant.addContent(Jskills);
				List<String> skillList = player.getSkills();
				for (int k = 0; k < skillList.size(); k++) {
					String skill = skillList.get(k);
					Element Jskill = new Element("skill");
					Jskill.setText(skill);
					Jskills.addContent(Jskill);
				}
			}
			Element scores = new Element("scores");
			equipe.addContent(scores);
			// Pour chaque score
			for (int j = 0; j < teamList.get(i).getScores().size(); j++) {
				Element score = new Element("score");
				scores.addContent(score);
				Score support = teamList.get(i).getScores().get(j);
				Element compet = new Element("competition");
				compet.setText(support.getEpreuve());
				score.addContent(compet);
				Element classement = new Element("classement");
				classement.setText(String.valueOf(support.getClassement()));
				score.addContent(classement);
				Element result = new Element("Nb_victoire");
				result.setText(String.valueOf(support.getNbVictoire()));
				score.addContent(result);
				Element points = new Element("Nb_points");
				result.setText(String.valueOf(support.getNbPoints()));
				score.addContent(points);
			}
		}
		return true;
	}

	/**
	 * Display all the teams
	 * 
	 * @return boolean
	 */
	public static boolean displayTeams() {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(teamDoc, System.out);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 * @param myFile
	 * @return
	 */
	public static List<Equipe> getTeamsFromXml(String myFile) {
		SAXBuilder sxb = new SAXBuilder();
		// On crée une liste d equipes
		List<Equipe> teamList = new ArrayList<Equipe>();
		try {
			// On crée un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est terminé ;)
			teamDoc = sxb.build(new File(dir + "teams.xml"));
			// On initialise un nouvel élément racine avec l'élément racine du
			// document.
			teamRoot = teamDoc.getRootElement();
			// On crée une List contenant tous les noeuds "equipe" de l'Element
			// racine
			@SuppressWarnings("unchecked")
			List<Element> tl = teamRoot.getChildren("equipe");
			// On crée un Iterator sur notre liste
			Iterator<Element> it = tl.iterator();
			while (it.hasNext()) {
				// On recrée l'Element courant à chaque tour de boucle afin de
				// pouvoir utiliser les méthodes propres aux Elements comme :
				// selectionner un noeud fils, modifier du texte, etc...
				Element currentTeam = it.next();
				// On recupere l'id de l'equipe
				int id = Integer.valueOf(currentTeam.getChild("id").getText());
				// On recupere le nom de l equipe
				String teamName = currentTeam.getChild("name").getText();
				// Puis la selection d'epreuve
				List<String> select = new ArrayList<String>();
				Element sel = currentTeam.getChild("selection");
				@SuppressWarnings("unchecked")
				List<Element> elList = sel.getChildren("competition");
				Iterator<Element> i = elList.iterator();
				while (i.hasNext()) {
					Element curCompet = i.next();
					String compet = curCompet.getText();
					select.add(compet);
				}
				// On va recuperer les joueurs maintenant
				Element participants = (Element) currentTeam
						.getChild("participants");
				List<Joueur> playerList = new ArrayList<Joueur>();
				@SuppressWarnings("unchecked")
				List<Element> pl = participants.getChildren("participant");
				Iterator<Element> j = pl.iterator();
				while (j.hasNext()) {
					Element currentPlayer = j.next();
					String playerName = currentPlayer.getChild("name")
							.getText();

					List<String> skills = new ArrayList<String>();
					Element pSkills = currentPlayer.getChild("skills");
					@SuppressWarnings("unchecked")
					List<Element> elemList = pSkills.getChildren("skill");
					Iterator<Element> iter = elemList.iterator();
					while (iter.hasNext()) {
						Element curSkill = iter.next();
						String skill = curSkill.getChild("skill").getText();
						skills.add(skill);
					}
					Joueur player = new Joueur(playerName, skills);
					// On ajoute le joueur a la liste de joueurs
					playerList.add(player);
				}
				// On crée une Liste de scores
				List<Score> scoreList = new ArrayList<Score>();
				Element scores = (Element) currentTeam.getChild("scores");
				@SuppressWarnings("unchecked")
				List<Element> sl = scores.getChildren("score");
				Iterator<Element> k = sl.iterator();
				while (k.hasNext()) {
					Element currentScore = k.next();
					// On ajoute le score a la liste de scores
					scoreList.add(new Score(currentScore
							.getChild("competition").getText(), Integer
							.valueOf(currentScore.getChild("classement")
									.getText()), Integer.valueOf(currentScore
							.getChild("Nb_victoire").getText()), Integer
							.valueOf(currentScore.getChild("Nb_points")
									.getText())));
				}
				// On peut alors ajouter la liste de joueurs et de scores a
				// l'equipe courante
				// On ajoute l equipe a la liste d equipes
				teamList.add(new Equipe(id, teamName, select, playerList,
						scoreList));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return teamList;
	}

	/**
	 * Enregistre 'competitions.xml'
	 * 
	 * @param fichier
	 * @return true si bien enregistré dans le fichier
	 */
	private static boolean recordCompets(String fichier) {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			// On crée une instance de FileOutputStream
			// avec en argument le nom du fichier pour effectuer la
			// sérialisation.
			sortie.output(competDoc, new FileOutputStream(fichier));
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * méthode statique ajoutant absolument toutes les infos du tournoi a
	 * l'exception des joueurs dans l'arborescence xml du document jdom
	 * en-dessous du noeud racine competitions
	 * 
	 * @param competList
	 * @return boolean
	 */
	private static boolean writeCompetsToXmlFile(List<Epreuve> competList) {
		// Pour chaque epreuve
		for (int i = 0; i < competList.size(); i++) {
			Epreuve ep = competList.get(i);
			Element compet = new Element("competition");
			competRoot.addContent(compet);
			Element id = new Element("id");
			id.setText(String.valueOf(ep.getCompetID()));
			compet.addContent(id);
			Element nameCompet = new Element("name");
			nameCompet.setText(ep.getNom());
			compet.addContent(nameCompet);
			Element type = new Element("type");
			nameCompet.setText(ep.getType());
			compet.addContent(type);
			Element indiv = new Element("individuelle");
			if (ep.isIndividuelle())
				indiv.setText("oui");
			else
				indiv.setText("non");
			compet.addContent(indiv);
			Element duree = new Element("duree");
			duree.setText(String.valueOf(ep.getDuree()));
			compet.addContent(duree);
			Element hStrt = new Element("debut");
			hStrt.setText(ep.getHourStart());
			compet.addContent(hStrt);
			Element pts = new Element("reward");
			pts.setText(String.valueOf(ep.getPointsWinner()).concat(" points"));
			compet.addContent(pts);
			// Sauvegarde des équipes ayant participé à cette épreuve
			Element equipes = new Element("teams");
			compet.addContent(equipes);
			List<Equipe> teamList = ep.getEquipes();
			// Pour chaque équipe
			for (int j = 0; j < teamList.size(); j++) {
				Equipe team = teamList.get(j);
				Element equipe = new Element("team");
				equipes.addContent(equipe);
				Element teamId = new Element("id");
				id.setText(String.valueOf(team.getTeamID()));
				equipe.addContent(teamId);
				Element nameTeam = new Element("name");
				equipe.addContent(nameTeam);
				nameTeam.setText(team.getNom());
				Element selection = new Element("selection");
				equipe.addContent(selection);
				List<String> selList = team.getSelectionOfEpreuves();
				for (int k = 0; k < selList.size(); k++) {
					String comp = selList.get(k);
					Element competition = new Element("competition");
					competition.setText(comp);
					selection.addContent(competition);
				}
				Element participants = new Element("participants");
				equipe.addContent(participants);
				List<Joueur> pList = team.getMembres();
				// Pour chaque joueur
				for (int k = 0; k < pList.size(); k++) {
					Joueur player = pList.get(k);
					Element participant = new Element("participant");
					participants.addContent(participant);
					Element nom = new Element("name");
					nom.setText(player.getNom());
					participant.addContent(nom);
					Element Jskills = new Element("skills");
					participant.addContent(Jskills);
					List<String> skillList = player.getSkills();
					for (int l = 0; l < skillList.size(); l++) {
						String skill = skillList.get(l);
						Element Jskill = new Element("skill");
						Jskill.setText(skill);
						Jskills.addContent(Jskill);
					}
				}
				Element scores = new Element("scores");
				equipe.addContent(scores);
				List<Score> scList = team.getScores();
				// Pour chaque score
				for (int k = 0; k < scList.size(); k++) {
					Score sc = scList.get(k);
					Element score = new Element("score");
					scores.addContent(score);
					Element compt = new Element("competition");
					compt.setText(sc.getEpreuve());
					score.addContent(compt);
					Element classement = new Element("classement");
					classement.setText(String.valueOf(sc.getClassement()));
					score.addContent(classement);
					Element result = new Element("Nb_victoire");
					result.setText(String.valueOf(sc.getNbVictoire()));
					score.addContent(result);
					Element points = new Element("Nb_points");
					result.setText(String.valueOf(sc.getNbPoints()));
					score.addContent(points);
				}
			}
			// Sauvegarde de la poule d'arbitres de l'épreuve
			List<Arbitre> refList = ep.getRefPool().getPouleArbitres();
			Element referees = new Element("referees");
			compet.addContent(referees);
			// Pour chaque arbitre
			for (int j = 0; j < refList.size(); j++) {
				Arbitre referee = refList.get(j);
				Element ref = new Element("referee");
				referees.addContent(ref);
				Element nameRef = new Element("name");
				ref.addContent(nameRef);
				nameRef.setText(referee.getNom());
				Element skillRef = new Element("skill");
				ref.addContent(skillRef);
				nameRef.setText(referee.getSkill());
			}
			Element rounds = new Element("rounds");
			compet.addContent(rounds);
			Map<Integer, List<Match>> matchMap = ep.getMatches();
			// Pour chaque tour
			for (int j = 0; j < matchMap.size(); j++) {
				Element round = new Element("round");
				rounds.addContent(round);
				Element numRound = new Element("number");
				round.addContent(numRound);
				numRound.setText(String.valueOf(j + 1));
				Element matches = new Element("matches");
				round.addContent(matches);
				List<Match> matchList = matchMap.get(new Integer(j + 1));
				if (matchList != null) {
					int stop = matchList.size();
					// Pour chaque match du tour
					for (int k = 0; k < stop; k++) {
						// Et on descend dans l arborescence xml
						// en suivant celle des classes Epreuves, Equipe, Match,
						// Score
						Match m = matchList.get(k);
						Element match = new Element("match");
						matches.addContent(match);
						Element numMatch = new Element("number");
						match.addContent(numMatch);
						numMatch.setText(String.valueOf(m.getNumero()));
						Element referee = new Element("referee");
						match.addContent(referee);
						referee.setText(m.getRef().getNom());
						Element teamA = new Element("team_A");
						match.addContent(teamA);
						Element nameA = new Element("name");
						nameA.setText(m.getEquipeA().getNom());
						teamA.addContent(nameA);
						Element scoreA = new Element("score");
						teamA.addContent(scoreA);
						Element classementA = new Element("classement");
						classementA.setText(String.valueOf(m.getEquipeA()
								.getScore(ep.getNom()).getClassement()));
						scoreA.addContent(classementA);
						Element resultA = new Element("Nb_victoires");
						resultA.setText(String.valueOf(m.getEquipeA()
								.getScore(ep.getNom()).getNbVictoire()));
						scoreA.addContent(resultA);
						Element ptsA = new Element("Nb_points");
						resultA.setText(String.valueOf(m.getEquipeA()
								.getScore(ep.getNom()).getNbPoints()));
						scoreA.addContent(ptsA);
						Element teamB = new Element("team_B");
						match.addContent(teamB);
						Element nameB = new Element("name");
						nameB.setText(m.getEquipeB().getNom());
						teamB.addContent(nameB);
						Element scoreB = new Element("score");
						teamB.addContent(scoreB);
						Element classementB = new Element("classement");
						classementB.setText(String.valueOf(m.getEquipeB()
								.getScore(ep.getNom()).getClassement()));
						scoreB.addContent(classementB);
						Element resultB = new Element("Nb_victoires");
						resultB.setText(String.valueOf(m.getEquipeB()
								.getScore(ep.getNom()).getNbVictoire()));
						scoreB.addContent(resultB);
						Element ptsB = new Element("Nb_points");
						resultB.setText(String.valueOf(m.getEquipeB()
								.getScore(ep.getNom()).getNbPoints()));
						scoreB.addContent(ptsB);
						Element teamWin = new Element("winner_team");
						match.addContent(teamWin);
						teamWin.setText(m.getVainqueur().getNom());
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean displayCompetitions() {
		try {
			// On utilise ici un affichage classique avec getPrettyFormat()
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(competDoc, System.out);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Lecture des épreuves depuis la structure JDOM
	 * 
	 * @param myFile
	 * @return boolean
	 */
	public static List<Epreuve> readCompetsFromXmlFile(String myFile) {
		SAXBuilder sxb = new SAXBuilder();
		List<Epreuve> competList = new ArrayList<Epreuve>();
		try {
			competDoc = sxb.build(new File(dir + "competitions.xml"));
			competRoot = competDoc.getRootElement();
			// On cree la liste d enfants du noeud racine competitions
			@SuppressWarnings("unchecked")
			List<Element> cl = competRoot.getChildren("competition");
			// On cree l iterateur associe a la liste
			Iterator<Element> i = cl.iterator();
			// On parcourt la liste des enfants de la racine
			// Pour chaque épreuve
			while (i.hasNext()) {
				Element currentCompet = i.next();
				int id = Integer
						.valueOf(currentCompet.getChild("id").getText());
				String competName = currentCompet.getChild("name").getText();
				String competType = currentCompet.getChild("type").getText();
				boolean individuelle = false;
				if (currentCompet.getChild("individuelle").getText()
						.equals("oui"))
					individuelle = true;
				int duree = Integer.valueOf(currentCompet.getChild("duree")
						.getText());
				String hStart = currentCompet.getChild("debut").getText();
				String pts = currentCompet.getChild("reward").getText();
				int points = Integer
						.valueOf(pts.substring(0, pts.indexOf(" ")));
				// On récupère toutes les équipes de l'épreuve
				List<Equipe> teamList = new ArrayList<Equipe>();
				Element teams = currentCompet.getChild("teams");
				@SuppressWarnings("unchecked")
				List<Element> el = teams.getChildren("team");
				Iterator<Element> iterator = el.iterator();
				// Pour chaque équipe
				while (iterator.hasNext()) {
					Element currentTeam = iterator.next();
					int teamID = Integer
							.valueOf(currentTeam.getChildText("id"));
					String teamName = currentTeam.getChildText("name");
					Element sel = currentTeam.getChild("selection");
					List<String> selection = new ArrayList<String>();
					@SuppressWarnings("unchecked")
					List<Element> selList = sel.getChildren("competition");
					Iterator<Element> it = selList.iterator();
					while (it.hasNext()) {
						Element selItem = it.next();
						String item = selItem.getText();
						selection.add(item);
					}
					Element participants = currentTeam.getChild("participants");
					List<Joueur> plList = new ArrayList<Joueur>();
					@SuppressWarnings("unchecked")
					List<Element> partList = participants
							.getChildren("participant");
					Iterator<Element> iter = partList.iterator();
					while (iter.hasNext()) {
						Element participant = iter.next();
						String name = participant.getChildText("name");
						List<String> skills = new ArrayList<String>();
						@SuppressWarnings("unchecked")
						List<Element> skillList = participant
								.getChildren("skills");
						Iterator<Element> iterat = skillList.iterator();
						while (iterat.hasNext()) {
							Element e = iterat.next();
							String skill = e.getChildText("skill");
							skills.add(skill);
						}
						plList.add(new Joueur(name, skills));
					}

					// les scores

					teamList.add(new Equipe(teamID, teamName, selection,
							plList, null));
				}
				// On récupère les arbitres
				List<Arbitre> refList = new ArrayList<Arbitre>();
				Element referees = currentCompet.getChild("referees");
				@SuppressWarnings("unchecked")
				List<Element> rl = referees.getChildren("referee");
				Iterator<Element> j = rl.iterator();
				while (j.hasNext()) {
					Element currentRef = j.next();
					refList.add(new Arbitre(currentRef.getChild("name")
							.getText(), currentRef.getChild("skill").getText()));
				}
				// On recree la poule d arbitres
				PouleArbitres refereePool = new PouleArbitres(refList);
				// On cree une map vide
				HashMap<Integer, List<Match>> matchMap = new HashMap<Integer, List<Match>>();
				Element rounds = (Element) currentCompet.getChild("rounds");
				@SuppressWarnings("unchecked")
				List<Element> roundList = rounds.getChildren("round");
				Iterator<Element> k = roundList.iterator();
				// numero du tour
				while (k.hasNext()) {
					Element currentRound = (Element) k.next();
					Integer roundNumber = new Integer(currentRound.getChild(
							"number").getText());
					// On cree une liste de matchs vide
					List<Match> matchList = new ArrayList<Match>();
					Element matches = (Element) currentRound
							.getChild("matches");
					@SuppressWarnings("unchecked")
					List<Element> ml = matches.getChildren("match");
					Iterator<Element> l = ml.iterator();
					while (l.hasNext()) {
						Element currentMatch = (Element) l.next();
						// On recupere le numero du match
						int matchNumber = Integer.valueOf(currentMatch
								.getChild("number").getText());
						// puis l arbitre
						Arbitre ref = new Arbitre(currentMatch.getChild(
								"referee").getText(), competName);
						// On cree une liste de scores vide
						List<Score> slA = new ArrayList<Score>();
						// On recupere le score de l equipe A
						Score scoreA = new Score(competName,
								Integer.valueOf(currentMatch.getChild("team_A")
										.getChild("score")
										.getChild("classement").getText()),
								Integer.valueOf(currentMatch.getChild("team_A")
										.getChild("score")
										.getChild("Nb_victoires").getText()),
								Integer.valueOf(currentMatch.getChild("team_A")
										.getChild("score")
										.getChild("Nb_points").getText()));
						// On l'ajoute à la liste de scores
						slA.add(scoreA);
						// On recrée l equipe A
						Equipe teamA = new Equipe(currentMatch
								.getChild("team_A").getChild("name").getText(),
								null, null, slA);
						// idem pour l'équipe B
						List<Score> slB = new ArrayList<Score>();
						Score scoreB = new Score(competName,
								Integer.valueOf(currentMatch.getChild("team_B")
										.getChild("score")
										.getChild("classement").getText()),
								Integer.valueOf(currentMatch.getChild("team_B")
										.getChild("score")
										.getChild("Nb_victoires").getText()),
								Integer.valueOf(currentMatch.getChild("team_B")
										.getChild("score")
										.getChild("Nb_points").getText()));
						slB.add(scoreB);
						Equipe teamB = new Equipe(currentMatch
								.getChild("team_B").getChild("name").getText(),
								null, null, slB);
						// Equipe winner = new
						// Equipe(currentMatch.getChild("winner_team").getText(),
						// null, null);
						// On recree le match
						Match m = new Match(matchNumber, competName, teamA,
								teamB, ref);
						// On l ajoute a la liste des matches
						matchList.add(m);
					}
					// On ajoute cette liste de matches dans la map au bon tour
					matchMap.put(roundNumber, matchList);
				}
				// / On recree l environnement du tournoi en recreant les
				// epreuves une a une
				Epreuve e = new Epreuve(id, competName, competType,
						individuelle, duree, hStart, points, teamList,
						matchMap, refereePool);
				if (e != null) {
					competList.add(e);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return competList;
	}

	/**
	 * saves the whole JDOM
	 * 
	 * @return boolean
	 */
	public static boolean saveAllXml() {
		if (saveRefs("referees.xml") && savePlayers("players.xml")
				&& saveTeams("teams.xml") && saveCompets("competitions.xml"))
			return true;
		return false;
	}

	/**
	 * Saves the Competition JDOM
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean saveCompets(String file) {
		if (writeCompetsToXmlFile(competList) && recordCompets(dir + file))
			return true;
		return false;
	}

	/**
	 * Saves the Team JDOM
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean saveTeams(String file) {
		if (writeTeamsToXmlFile(teamList) && recordTeams(dir + file))
			return true;
		return false;

	}

	/**
	 * Saves the Player JDOM
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean savePlayers(String file) {
		if (writePlayersToXmlFile(playerList) && recordPlayers(dir + file))
			return true;
		return false;
	}

	/**
	 * Saves the Ref JDOM
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean saveRefs(String file) {
		if (writeRefsToXmlFile(refList) && recordReferees(dir + file))
			return true;
		return false;
	}
}
