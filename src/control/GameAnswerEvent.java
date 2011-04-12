package control;

import java.util.EventObject;

@SuppressWarnings("serial")
public class GameAnswerEvent extends EventObject {

	private int[] results;
	private int pos;
	private char bonus;
	private String sol;
	private String def;
	
	private AnswerType type;
	
	public enum AnswerType {
		
		CMP_PROPOSAL,WORD_FOUND,END_GAME,BONUS_GIVEN,NO_MORE_TRY;
	
	}
	
	public GameAnswerEvent(Object source,AnswerType type) {
		super(source);
		this.type = type;
	}
	
	public GameAnswerEvent(Object source,int[] res) {
		this(source,AnswerType.CMP_PROPOSAL);
		results = res;
	}
	
	public GameAnswerEvent(Object source,int n,char c,int[] res) {
		this(source,AnswerType.BONUS_GIVEN);
		pos = n;
		bonus = c;
		results = res;
	}
	
	public GameAnswerEvent(Object source,AnswerType type,String rep,String def) {
		this(source,type);
		sol = rep;
		this.def = def;
	}
	
	public AnswerType getCause() {
		return type;
	}
	
	public int[] getResults() {
		return results;
	}
	
	public int getPos() {
		return pos;
	}
	
	public char getBonus() {
		return bonus;
	}
	
	public String getSol() {
		return sol;
	}
	
	public String getDef() {
		return def;
	}
}
