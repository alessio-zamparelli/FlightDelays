package it.polito.tdp.extflightdelays.db;

import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airport;

public class TestDAO {

	public static void main(String[] args) {

		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();

		System.out.println(dao.loadAllAirlines());
		
//		System.out.println(dao.loadAllAirlines());
//		System.out.println(dao.loadAllAirports());
	}

}
