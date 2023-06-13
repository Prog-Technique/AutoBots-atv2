package com.autobots.automanager.modelo;

import java.util.List;


public interface Adicionador<T> {
	
	public void adicionarLink(List<T> lista);
	public void adicionarLink(T objeto);
	
}