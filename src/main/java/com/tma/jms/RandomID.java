package com.tma.jms;

import java.util.Random;
import java.util.Vector;

public class RandomID {
	public String get() {
		Vector <Character>randomCharacters=new Vector<Character>();
		for(int i=0;i<40;i++)randomCharacters.add((char) ((new Random().nextInt(26))+97));
		String randName= "";
		for(Character a:randomCharacters) randName  = randName + a;
		return randName;
	}
}
