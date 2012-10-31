package Dominio;

import java.util.Arrays;

public class Tauler
{

	protected int mida;
	protected EstatCasella[][] caselles;
	protected int num_fitxes_a;
	protected int num_fitxes_b;

	/**
	 * Constructor del tauler. Crea un tauler de la mida desitjada amb totes les caselles buides (EstatCasella.BUIDA).
	 *
	 * @param mida Les dimensions que tindrà el tauler
	 */
	public Tauler( int mida )
	{
		this.mida = mida;
		num_fitxes_a = 0;
		num_fitxes_b = 0;

		caselles = new EstatCasella[mida][mida];
		for ( EstatCasella[] fila : caselles )
		{
			Arrays.fill( fila, EstatCasella.BUIDA );
		}
	}

	/**
	 * Constructor que inicialitza el tauler a un estat diferent del per defecte. No comprova que els paràmetres siguin
	 * correctes.
	 *
	 * @param mida         Les dimensions del tauler
	 * @param caselles     Un array bidimensional mida × mida amb l'estat inicial
	 * @param num_fitxes_a La quantitat de fitxes que té el jugador A al tauler
	 * @param num_fitxes_b La quantitat de fitxes que té el jugador B al tauler
	 */
	public Tauler( int mida, EstatCasella[][] caselles, int num_fitxes_a, int num_fitxes_b )
	{
		this.mida = mida;
		this.num_fitxes_a = num_fitxes_a;
		this.num_fitxes_b = num_fitxes_b;
		this.caselles = caselles;
	}

	/**
	 * Consulta la mida del tauler
	 *
	 * @return La mida del tauler
	 */
	public int getMida()
	{
		return mida;
	}

	/**
	 * Comprova si una casella és vàlida dins el tauler
	 *
	 * @param fila    Fila de la casella dins el tauler.
	 * @param columna Columna de la casella dins el tauler.
	 * @return Cert si la posició (fila, columna) és una casella vàlida. Fals altrament.
	 */
	public boolean esCasellaValida( int fila, int columna )
	{
		return ( fila >= 0 && fila < mida && columna >= 0 && columna < mida );
	}

	/**
	 * Consulta si el tauler és buit
	 *
	 * @return Cert si el tauler no té cap fitxa. Fals altrament.
	 */
	public boolean esBuit()
	{
		return ( num_fitxes_a + num_fitxes_b == 0 );
	}

	/**
	 * Consulta les fitxes del jugador A.
	 *
	 * @return La quantitat de fitxes del jugador A.
	 */
	public int getNumFitxesA()
	{
		return num_fitxes_a;
	}

	/**
	 * Consulta les fitxes del jugador B.
	 *
	 * @return La quantitat de fitxes del jugador A.
	 */
	public int getNumFitxesB()
	{
		return num_fitxes_b;
	}

	/**
	 * Consulta la quantitat de fitxes que hi ha al tauler.
	 *
	 * @return La quantitat total de fitxes que tenen els dos jugadors al tauler.
	 */
	public int getTotalFitxes()
	{
		return num_fitxes_a + num_fitxes_b;
	}

	/**
	 * Consulta l'estat d'una casella del tauler.
	 *
	 * @param fila    Fila de la casella del tauler que es vol consultar.
	 * @param columna Columna de la casella del tauler que es vol consultar.
	 * @return L'estat actual de la casella.
	 */
	public EstatCasella getEstatCasella( int fila, int columna )
	{
		return caselles[fila][columna];
	}

	/**
	 * Canvia l'estat d'una casella i actualitza els comptadors.
	 *
	 * @param e       Estat nou de la casella
	 * @param fila    Fila de la casella del tauler que canvia d'estat
	 * @param columna Columna de la casella del tauler que canvia d'estat
	 * @return Cert si el canvi ha estat realitzat amb èxit. Fals altrament.
	 */
	public boolean setEstatCasella( EstatCasella e, int fila, int columna )
	{
		if ( !esCasellaValida( fila, columna ) )
		{
			return false;
		}

		switch ( caselles[fila][columna] )
		{
			case JUGADOR_A:
				num_fitxes_a--;
				break;
			case JUGADOR_B:
				num_fitxes_b--;
				break;
			case BUIDA:
				break;
		}

		switch ( e )
		{
			case JUGADOR_A:
				num_fitxes_a++;
				break;
			case JUGADOR_B:
				num_fitxes_b++;
				break;
			case BUIDA:
				break;
		}

		caselles[fila][columna] = e;

		return true;
	}

	/**
	 * Intercanvia la fitxa d'una casella amb la de l'altre jugador i actualitza els comptadors.
	 *
	 * @param fila    Fila de la casella dins el tauler.
	 * @param columna Columna de la casella dins el tauler.
	 * @return Cert si s'ha intercanviat la fitxa. Fals altrament.
	 */
	public boolean intercanviaFitxa( int fila, int columna )
	{
		if ( !esCasellaValida( fila, columna ) || caselles[fila][columna] == EstatCasella.BUIDA )
		{
			return false;
		}
		else if ( caselles[fila][columna] == EstatCasella.JUGADOR_A )
		{
			caselles[fila][columna] = EstatCasella.JUGADOR_B;
			num_fitxes_a--;
			num_fitxes_b++;
		}
		else
		{
			caselles[fila][columna] = EstatCasella.JUGADOR_A;
			num_fitxes_b--;
			num_fitxes_a++;
		}

		return true;
	}
}
