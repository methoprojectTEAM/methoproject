package com.example.niephox.methophotos.Interfaces;

import java.util.Observable;

public interface Subject {
	public void register(Observer observer);
	public void unregister(Observer observer);
	public void notifyObservers();
}
