package de.fh.stud;

import de.fh.kiServer.agents.Agent;
import de.fh.kiServer.util.Util;
import de.fh.pacman.PacmanAgent;
import de.fh.pacman.PacmanGameResult;
import de.fh.pacman.PacmanPercept;
import de.fh.pacman.PacmanStartInfo;
import de.fh.pacman.enums.PacmanAction;
import de.fh.pacman.enums.PacmanActionEffect;
import de.fh.stud.p2.Knoten;
import de.fh.stud.p3.Suche;
import de.fh.stud.p3.Suchverfahren;

import java.util.Stack;

public class MyAgent_P3 extends PacmanAgent {
	
	Stack<PacmanAction> actions = new Stack <PacmanAction>();
	
	private Knoten loesungsKnoten;
	
	/**
	 * Konstruktor f�r die Klasse MyAgent_P3, mit namen f�r den Agenten
	 * 
	 * @param name Der Name des Agenten, der angezeigt werden soll
	 */
	public MyAgent_P3(String name) {
		super(name);
	}
	
	/**
	 * main Funktion die beim Start des Programms ausgef�hrt wird.
	 * 
	 * @param args Ein Array vom Typ String mit den Argumenten, welche beim Programmstart �bergeben werden.
	 */
	public static void main(String[] args) {
		MyAgent_P3 agent = new MyAgent_P3("Moritz Agent");
		Agent.start(agent, "127.0.0.1", 5000);
	}

	/**
	 * Eine Methode, welche die n�chste durchzuf�hrende Aktion berechnet.
	 * 
	 * Funktionsweise:
	 * Ist noch kein L�sungsknoten gefunden, so wird eine neue Suche initialisiert,
	 * und diese auch Mithilfe der Methode start() der Klasse Suche gestartet.
	 * 
	 * Findet die Suche einen L�sungsknoten, so wird der L�sungsweg f�r diesen Knoten in der Liste actions gespeichert.
	 * Wird kein L�sungsknoten gefunden, so wird die Liste actions Sicherheitshalber geleert.
	 * 
	 * Nachdem die Suche abgeschlossen ist, bzw wenn schon ein L�sungsknoten da ist,
	 * wird aus der Liste actions die n�chste durchzuf�hrende Aktion rausgeholt und zur�ck gegeben.
	 * 
	 * @param percept
	 * @param actionEffect
	 * @return Ein PacmanAction wert, welcher die n�chste durchzuf�hrende Aktion wiederspiegelt
	 */
	@Override
	public PacmanAction action(PacmanPercept percept, PacmanActionEffect actionEffect) {
		
		Util.printView(percept.getView());
		
		if (loesungsKnoten == null) {
			Suche suche = new Suche(new Knoten(percept, null));
			loesungsKnoten = suche.start(Suchverfahren.A_STERN);
			
			if(loesungsKnoten != null) {
				actions.addAll(loesungsKnoten.getLoesungsweg());
			}else {
				actions.clear();
			}
		}
		
		if(!actions.isEmpty()) {
			return actions.pop();
		}else {
			return PacmanAction.QUIT_GAME;
		}
	}

	@Override
	protected void onGameStart(PacmanStartInfo startInfo) {
		
	}

	/**
	 * Eine Methode die aufgerufen wird, wenn das Spiel vorbei ist.
	 * 
	 * Funktionsweise:
	 * Diese Methode dient uns zur Ausgabe der Anzahl der Z�ge,
	 * sowie der Anzahl der Elemente in sowohl der open- als auch der closed-List
	 */
	@Override
	protected void onGameover(PacmanGameResult gameResult) {
		gameResult.print();
		System.out.println("Open List: " + Suche.openList.size());
		System.out.println("Closed List: " + Suche.closedList.size());
	}
	
}
