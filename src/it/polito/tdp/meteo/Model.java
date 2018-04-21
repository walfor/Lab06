package it.polito.tdp.meteo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteodao;
	private List<Rilevamento> rilevamenti;
	private List<SimpleCity> soluzione;
	private List<SimpleCity> parziale;
	private List<Citta> citta;

	public Model() {
		this.rilevamenti = new ArrayList();
		this.meteodao = new MeteoDAO();
		this.parziale = new ArrayList();
		citta = this.meteodao.getAllCitta();
		this.soluzione = new ArrayList();

	}

	public String getUmiditaMedia(int mese) {

		this.rilevamenti = this.meteodao.getAllRilevamenti();
		Integer[] array = new Integer[3];

		for (int i = 0; i < 3; i++) {
			array[i] = 0;
		}
		double sommaTorino = 0;
		double sommaGenova = 0;
		double sommaMilano = 0;
		int torino = 0;
		int genova = 0;
		int milano = 0;
		for (Rilevamento r : this.rilevamenti) {

			if (r.getLocalita().equals("Torino") && r.getData().getMonth() == mese) {
				torino++;

				// array[0]+=r.getUmidita();
				sommaTorino += r.getUmidita();
			} else if (r.getLocalita().equals("Genova")) {
				sommaGenova += r.getUmidita();
				// array[1]+=r.getUmidita();
				genova++;

			} else if (r.getLocalita().equals("Milano")) {
				sommaMilano += r.getUmidita();
				// array[2]+=r.getUmidita();
				milano++;
			}

		}
		// double mediaT = array[0] / torino;
		// double mediaG = array[1] / genova;
		// double mediaM = array[2] / milano;

		double mediaT = sommaTorino / torino;
		double mediaG = sommaTorino / genova;
		double mediaM = sommaGenova / milano;

		return String.format(
				"Rilavamento per la citta' di torino: %f \n Rilevamento per la citta' di genova: %f \n Rilevamento per la città di Milano: %f",
				mediaT, mediaG, mediaM);
	}

	public String trovaSequenza(int mese) {

		for (Citta c : this.citta) {

			c.setRilevamenti(this.meteodao.getAllRilevamentiLocalita(c));

		}
		for (Citta c : this.citta) {
			SimpleCity s = new SimpleCity(c, mese);
			parziale.add(s);
			recursive(0, parziale, mese);

		}

		return this.soluzione.toString();
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		return score;
	}

	/**
	 * Ha il compito di andare a vedere se ci sono almeno tre ripetizioni dell'
	 * ultima citta visitata, altrimenti non si puo' cambiare citta
	 * 
	 * @param parziale
	 * @return
	 */
	private boolean controllaParziale(List<SimpleCity> parziale) {
		int contaConsecutive = 0;

		for (int i = parziale.size() - 1; i > 0; i--) {
			if (parziale.get(i).equals(parziale.get(i - 1))) {

				contaConsecutive++;
				if (contaConsecutive >= 3) {
					return true;
				}

			} else
				return false;
		}
		return false;
	}

	private void recursive(int step, List<SimpleCity> parziale, int mese) {

		// doalways calcolo la lista citta

		if (step >= 15) {
			return;
		}

		if (soluzioneAccettabile(parziale) == this.citta.size()) {
			this.soluzione.addAll(parziale);
			return;
		}

		for (Citta c : this.citta) {
			SimpleCity s = new SimpleCity(c, mese);
			parziale.add(s);

			if (conta(parziale, parziale.get(step)) < 6) {

				if (step >= 2) {
					if (controllaParziale(parziale))
						recursive(step + 1, parziale, mese);
				}

			}
			parziale.remove(s);

		}
	}

	private int soluzioneAccettabile(List<SimpleCity> parziale) {
		int contadiverse = 0;
		for (int i = parziale.size() - 1; i > 0; i--) {

			if (!parziale.get(i).equals(parziale.get(i - 1))) {
				contadiverse++;
			}

		}
		return contadiverse;
	}

	private int conta(List<SimpleCity> parziale, SimpleCity simpleCity) {
		// TODO Auto-generated method stub
		int contatore = 0;
		for (SimpleCity s : parziale) {
			if (s.equals(simpleCity)) {
				contatore++;
			}
		}
		return contatore;

	}
}
