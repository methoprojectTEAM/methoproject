package com.example.niephox.methophotos.Interfaces;

public interface Observer {
	//TODO: observer function of update must be as abstract as possible to keep up with pattern best practices.
	//TODO: so, we should handle the code where the update is happening,
	void update(Object objectToCastTo);
}
