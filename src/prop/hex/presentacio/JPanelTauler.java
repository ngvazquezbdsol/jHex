package prop.hex.presentacio;

import prop.cluster.domini.models.estats.EstatCasella;
import prop.cluster.domini.models.estats.EstatPartida;
import prop.hex.domini.models.Casella;
import prop.hex.domini.models.enums.CombinacionsColors;
import prop.hex.domini.models.enums.ModesInici;
import prop.hex.domini.models.enums.TipusJugadors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class JPanelTauler extends JPanel
{

	private static PresentacioCtrl presentacio_ctrl;

	private Object[] elements_de_control_partida;

	private Object[][] elements_de_control_jugadors;

	private Casella ultima_pista;

	private boolean pista_valida;

	private JButton mou_ia;

	private JButton demana_pista;

	/**
	 * Poligon que s'utilitza per dibuixar a pantalla.
	 */
	private Polygon hexagon;

	/**
	 * dx i dy són els increments horitzontals i verticals entre caselles.
	 */
	private int dx, dy;

	/**
	 * Radi de les caselles.
	 */
	private double radi = 40.0;

	/**
	 * Posició inicial del taulell a la pantalla.
	 */
	private int iniciX = 90;
	private int iniciY = 80;

	private boolean partida_en_curs;

	private boolean partida_finalitzada;

	private boolean partida_ia;

	/**
	 * Constructora, obté el taulell i els jugadors, construeix un poligon hexagonal i
	 * afegeix el listener del ratoli pel cas del click.
	 */
	public JPanelTauler( boolean partida_en_curs, PresentacioCtrl presentacio_ctrl )
	{
		this.presentacio_ctrl = presentacio_ctrl;
		elements_de_control_partida = presentacio_ctrl.getElementsDeControlPartida();
		elements_de_control_jugadors = presentacio_ctrl.getElementsDeControlJugadors();
		ultima_pista = new Casella( 0, 0 );
		pista_valida = false;
		this.partida_en_curs = partida_en_curs;
		partida_finalitzada = false;
		partida_ia = ( ( TipusJugadors ) elements_de_control_jugadors[0][0] ) != TipusJugadors.JUGADOR &&
		             ( ( TipusJugadors ) elements_de_control_jugadors[0][0] ) != TipusJugadors.CONVIDAT &&
		             ( ( TipusJugadors ) elements_de_control_jugadors[0][1] ) != TipusJugadors.JUGADOR &&
		             ( ( TipusJugadors ) elements_de_control_jugadors[0][1] ) != TipusJugadors.CONVIDAT;

		//Creem l'hexàgon que dibuixarem després.
		int x[] = new int[6];
		int y[] = new int[6];
		for ( int i = 0; i < 6; i++ )
		{
			x[i] = ( int ) ( radi * Math.sin( ( double ) i * Math.PI / 3.0 ) );
			y[i] = ( int ) ( radi * Math.cos( ( double ) i * Math.PI / 3.0 ) );
		}
		hexagon = new Polygon( x, y, 6 );
		dx = ( int ) ( 2 * radi * Math.sin( Math.PI / 3.0 ) );
		dy = ( int ) ( 1.5 * radi );

		// Afegim el listener del ratoli.
		addMouseListener( new MouseAdapter()
		{

			@Override
			public void mouseClicked( MouseEvent mouseEvent )
			{
				comprovaClick( mouseEvent.getX(), mouseEvent.getY() );
			}
		} );
	}

	public void afegeixBotons( JButton mou_ia, JButton demana_pista )
	{
		this.mou_ia = mou_ia;
		this.demana_pista = demana_pista;
	}

	/**
	 * Calcula sobre quina casella es corresponen les coordenades píxel i crida a clickHexagon amb aquesta
	 * informació. Listener de mouseClicked. Si s'ha fet click sobre una casella crida a clickHexagon,
	 * si s'ha fet click sobre el botó de moviment IA, crida a mouIA
	 *
	 * @param x Coordenades píxel horitzontals.
	 * @param y Coordenades píxel verticals.
	 */
	private void comprovaClick( int x, int y )
	{
		if ( !partida_finalitzada )
		{
			int rx = iniciX;
			int ry = iniciY;

			elements_de_control_partida = presentacio_ctrl.getElementsDeControlPartida();
			elements_de_control_jugadors = presentacio_ctrl.getElementsDeControlJugadors();

			for ( int i = 0; i < ( Integer ) elements_de_control_partida[1]; i++ )
			{
				rx += i * dx / 2;
				ry += i * dy;
				for ( int j = 0; j < ( Integer ) elements_de_control_partida[1]; j++ )
				{
					rx += j * dx;
					if ( hexagon.contains( x - rx, y - ry ) )
					{
						clickHexagon( i, j );
					}
					rx -= j * dx;
				}
				rx -= i * dx / 2;
				ry -= i * dy;
			}
		}
	}

	private boolean potDemanarPista()
	{
		int i = ( Integer ) elements_de_control_partida[2] % 2;
		return ( partida_en_curs && !pista_valida && presentacio_ctrl.esTornHuma() &&
				presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA &&
				( ( Integer ) elements_de_control_partida[0] > ( Integer ) elements_de_control_jugadors[1][i] ) );
	}

	private boolean potMoureIA()
	{
		return ( partida_en_curs && ( partida_ia || ( Integer ) elements_de_control_partida[2] == 0 ||
			presentacio_ctrl.esPartidaAmbSituacioInicialAcabadaDeDefinir() ) && !presentacio_ctrl.esTornHuma() &&
			presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA );
	}

	/**
	 * Si la partida no està finalitzada i es torn de una IA, crida PartidaCtrl a executar moviment IA.
	 * Torna a pintar l'escena.
	 */
	public void mouIAOMostraPista()
	{
		if ( presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA && !presentacio_ctrl.esTornHuma() )
		{
			presentacio_ctrl.executaMovimentIA();
		}
		else if ( presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA &&
		          presentacio_ctrl.esTornHuma() )
		{
			ultima_pista = presentacio_ctrl.obtePista();
			pista_valida = true;
		}

		repaint();
	}

	/**
	 * Si la partida no està finalitzada i es torn d'un huma, crida a fer moviment a la casella i, j.
	 * Torna a pintar l'escena.
	 *
	 * @param i fila casella.
	 * @param j columna casella.
	 */
	private void clickHexagon( int i, int j )
	{
		if ( presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA &&
		     ( presentacio_ctrl.esTornHuma() || !partida_en_curs ) )
		{
			try
			{
				//No ens cal comprovar si el moviment es fa o no (si retorna true o false).
				presentacio_ctrl.mouFitxa( i, j );
				pista_valida = false;
				paintImmediately( 0, 0, 800, 500 );
				if ( !partida_finalitzada && partida_en_curs && !presentacio_ctrl.esTornHuma() )
				{
					mouIAOMostraPista();
				}
			}
			catch ( UnsupportedOperationException exepcio )
			{
				System.out.println( "Moviment no vàlid, partida finalitzada: " + exepcio.getMessage() );
			}
		}

		if ( !partida_finalitzada )
		{
			repaint();
		}
	}

	/**
	 * Retorna la mida de la pantalla.
	 *
	 * @return
	 */
	public Dimension getPreferredSize()
	{
		return new Dimension( 800, 500 );
	}

	/**
	 * Pinta la pantalla.
	 *
	 * @param g Paràmetre Graphics on es pinta.
	 */
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		elements_de_control_partida = presentacio_ctrl.getElementsDeControlPartida();
		elements_de_control_jugadors = presentacio_ctrl.getElementsDeControlJugadors();

		//Dibuixem el tauler, pintant cada hexagon del color que toca.
		g.translate( iniciX, iniciY );
		for ( int i = 0; i < ( Integer ) elements_de_control_partida[1]; i++ )
		{
			g.translate( i * dx / 2, i * dy );
			for ( int j = 0; j < ( Integer ) elements_de_control_partida[1]; j++ )
			{
				g.translate( j * dx, 0 );

				if ( ( Integer ) elements_de_control_partida[2] == 0 &&
				     elements_de_control_partida[4] == ModesInici.ESTANDARD &&
				     presentacio_ctrl.esCasellaCentral( i, j ) )
				{
					g.setColor(
							( ( CombinacionsColors ) elements_de_control_partida[3] ).getColorCasellesInhabilitades() );
				}
				else
				{
					if ( pista_valida && ( ultima_pista.getFila() == i && ultima_pista.getColumna() == j ) )
					{
						g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] ).getColorCasellesPista() );
						ultima_pista.setColumna( 0 );
						ultima_pista.setFila( 0 );
					}
					else
					{
						g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
								.getColorCasella( presentacio_ctrl.getEstatCasella( i, j ) ) );
					}
				}
				g.fillPolygon( hexagon );

				g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] ).getColorVoraCaselles() );
				g.drawPolygon( hexagon );

				g.translate( -j * dx, 0 );
			}
			g.translate( -i * dx / 2, -i * dy );
		}

		if ( partida_en_curs )
		{
			if ( potMoureIA() )
			{
				mou_ia.setEnabled( true );
			}
			else
			{
				mou_ia.setEnabled( false );
			}

			if ( potDemanarPista() )
			{
				demana_pista.setEnabled( true );
			}
			else
			{
				demana_pista.setEnabled( false );
			}
		}

		// Si és torn de la IA i a la partida juga algun humà,
		if ( !( ( Integer ) elements_de_control_partida[2] == 0 ||
			presentacio_ctrl.esPartidaAmbSituacioInicialAcabadaDeDefinir() ) && partida_en_curs &&
			!partida_ia && !presentacio_ctrl.esTornHuma() &&
		     presentacio_ctrl.consultaEstatPartida() == EstatPartida.NO_FINALITZADA )
		{
			if ( ( Integer ) elements_de_control_partida[2] % 2 == 0 )
			{
				g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
						.getColorCasella( EstatCasella.JUGADOR_A ) );
				g.drawString( "Pensant moviment...", -50, 370 );
			}
			else
			{
				g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
						.getColorCasella( EstatCasella.JUGADOR_B ) );
				g.drawString( "Pensant moviment...", 580, 160 );
			}
		}

		//Mostrem el torn actual.
		g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
				.getColorTextInformacio( EstatCasella.BUIDA ) );
		g.drawString( "Torn: " + elements_de_control_partida[2], -50, 210 );
		g.drawString( "Torn: " + elements_de_control_partida[2], 580, 0 );

		//Mostrem algunes dades pel jugador A
		g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
				.getColorTextInformacio( EstatCasella.JUGADOR_A ) );
		if ( ( Integer ) elements_de_control_partida[2] % 2 == 0 )
		{
			g.drawString( "Té el torn", -50, 270 );
		}
		g.drawString( ( ( String ) elements_de_control_jugadors[3][0] ), -50, 290 );
		g.drawString( "D'esquerra a dreta", -50, 310 );

		g.drawString( "Temps: " + ( String ) elements_de_control_jugadors[2][0], -50, 330 );
		g.drawString( "Pistes disponibles: " + ( ( Integer ) elements_de_control_partida[0] -
		                                         ( ( Integer ) elements_de_control_jugadors[1][0] ) ), -50, 350 );

		//I algunes dades pel jugador B
		g.setColor( ( ( CombinacionsColors ) elements_de_control_partida[3] )
				.getColorTextInformacio( EstatCasella.JUGADOR_B ) );
		if ( ( Integer ) elements_de_control_partida[2] % 2 == 1 )
		{
			g.drawString( "Té el torn", 580, 60 );
		}
		g.drawString( ( ( String ) elements_de_control_jugadors[3][1] ), 580, 80 );
		g.drawString( "De dalt a baix", 580, 100 );

		g.drawString( "Temps: " + ( String ) elements_de_control_jugadors[2][1], 580, 120 );
		g.drawString( "Pistes disponibles: " + ( ( Integer ) elements_de_control_partida[0] -
		                                         ( ( Integer ) elements_de_control_jugadors[1][1] ) ), 580, 140 );

		if ( ( ( CombinacionsColors ) elements_de_control_partida[3] ) == CombinacionsColors.VERMELL_BLAU )
		{
			g.drawImage( ( new ImageIcon( "img/tauler_vb.png" ) ).getImage(), -iniciX, -iniciY, getWidth(), getHeight(),
					null );
		}
		else
		{
			g.drawImage( ( new ImageIcon( "img/tauler_nb.png" ) ).getImage(), -iniciX, -iniciY, getWidth(), getHeight(),
					null );
		}

		//Si ha guanyat un jugador, mostrem el resultat.
		if ( presentacio_ctrl.consultaEstatPartida() == EstatPartida.GUANYA_JUGADOR_A )
		{
			g.setColor( new Color( 0x66CCFF ) );
			g.fillRoundRect( 140, 0, 320, 200, 16, 16 );
			g.setColor( Color.black );
			g.drawRoundRect( 140, 0, 320, 200, 16, 16 );
			g.drawString( "Partida finalitzada.", 150, 30 );
			g.drawString( "Guanya " + ( ( String ) elements_de_control_jugadors[3][0] ), 150, 50 );
			g.drawString( "amb un temps de " + ( String ) elements_de_control_jugadors[2][0] + ",", 150, 70 );
			g.drawString( "col·locant un total de " + ( ( Integer ) elements_de_control_jugadors[4][0] ) + " fitxes,",
					150, 90 );
			g.drawString( "i utilitzant " + ( ( Integer ) elements_de_control_jugadors[1][0] ) + " pistes.", 150, 110 );
			g.drawString( "Per a continuar, pitja el botó Abandona partida.", 150, 150 );

			partida_finalitzada = true;
			try
			{
				presentacio_ctrl.finalitzaPartida();
			}
			catch ( Exception excepcio )
			{
				VistaDialeg dialeg_error = new VistaDialeg();
				String[] botons_error = { "Accepta" };
				dialeg_error.setDialeg( "Error", excepcio.getMessage(), botons_error, JOptionPane.WARNING_MESSAGE );
			}
		}
		else if ( presentacio_ctrl.consultaEstatPartida() == EstatPartida.GUANYA_JUGADOR_B )
		{
			g.setColor( new Color( 0x66CCFF ) );
			g.fillRoundRect( 140, 0, 320, 200, 16, 16 );
			g.setColor( Color.black );
			g.drawRoundRect( 140, 0, 320, 200, 16, 16 );
			g.drawString( "Partida finalitzada.", 150, 30 );
			g.drawString( "Guanya " + ( ( String ) elements_de_control_jugadors[3][1] ), 150, 50 );
			g.drawString( "amb un temps de " + ( String ) elements_de_control_jugadors[2][1] + ",", 150, 70 );
			g.drawString( "col·locant un total de " + ( ( Integer ) elements_de_control_jugadors[4][1] ) + " fitxes,",
					150, 90 );
			g.drawString( "i utilitzant " + ( ( Integer ) elements_de_control_jugadors[1][1] ) + " pistes.", 150, 110 );
			g.drawString( "Per a continuar, pitja el botó Abandona partida.", 150, 150 );

			partida_finalitzada = true;
			try
			{
				presentacio_ctrl.finalitzaPartida();
			}
			catch ( Exception excepcio )
			{
				VistaDialeg dialeg_error = new VistaDialeg();
				String[] botons_error = { "Accepta" };
				dialeg_error.setDialeg( "Error", excepcio.getMessage(), botons_error, JOptionPane.WARNING_MESSAGE );
			}
		}
	}
}
