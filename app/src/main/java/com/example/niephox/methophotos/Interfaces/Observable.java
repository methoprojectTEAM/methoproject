package com.example.niephox.methophotos.Interfaces;


public interface Observable {
	public void register(Observer observer);
	public void unregister(Observer observer);
	public void notifyObservers();
}
