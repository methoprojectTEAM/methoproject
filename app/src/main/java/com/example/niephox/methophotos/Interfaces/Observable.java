package com.example.niephox.methophotos.Interfaces;


public interface Observable {
	void register(Observer observer);
	void unregister(Observer observer);
	void notifyObservers();
}
