package prop.hex.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Vista del menú principal del joc Hex.
 *
 * @author Guillermo Girona San Miguel (Grup 7.3, Hex)
 */
public final class MenuPrincipalVista extends BaseVista
{

	// Panell

	/**
	 * Panell que conté els botons de la vista.
	 */
	private JPanel panell_botons;

	/**
	 * Panell que conté el botó de tancar la sessió.
	 */
	private JPanel panell_tanca_sessio;

	// Botons

	/**
	 * Botó de tanca sessió.
	 */
	private JButton tanca_sessio;

	/**
	 * Botó de juga una partida.
	 */
	private JButton juga;

	/**
	 * Botó de carrega una partida.
	 */
	private JButton carrega;

	/**
	 * Botó de configuració.
	 */
	private JButton configuracio;

	/**
	 * Botó de rànquing.
	 */
	private JButton ranquing;

	// Etiquetes de text
	private JLabel nom_jugador_principal;

	/**
	 * Constructor que crea una vista passant-li quin és el frame sobre el qual s'haurà de treballar.
	 *
	 * @param frame_principal Frame principal sobre el que s'hauran d'afegir els diferents components.
	 */
	public MenuPrincipalVista( JFrame frame_principal )
	{
		super( frame_principal );

		titol = new JLabel( "Menú principal" );
		panell_botons = new JPanel();
		panell_tanca_sessio = new JPanel();
		tanca_sessio = new JButton( "Tanca la sessió" );
		juga = new JButton( "Juga una partida" );
		carrega = new JButton( "Carrega una partida" );
		configuracio = new JButton( "Configuració" );
		ranquing = new JButton( "Rànquing" );

		inicialitzaVista();
	}

	@Override
	protected void inicialitzaPanellPrincipal()
	{
		panell_principal.setLayout( new GridBagLayout() );
		panell_principal.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		GridBagConstraints propietats_panel = new GridBagConstraints();
		propietats_panel.fill = GridBagConstraints.HORIZONTAL;
		propietats_panel.anchor = GridBagConstraints.CENTER;
		propietats_panel.gridx = 1;
		propietats_panel.gridy = 0;
		propietats_panel.weightx = 0.5;
		propietats_panel.weighty = 0.2;
		panell_principal.add( panell_titol, propietats_panel );
		propietats_panel.fill = GridBagConstraints.BOTH;
		propietats_panel.gridx = 1;
		propietats_panel.gridy = 1;
		propietats_panel.weighty = 0.6;
		panell_principal.add( panell_botons, propietats_panel );
		propietats_panel.fill = GridBagConstraints.NONE;
		propietats_panel.gridy = 2;
		propietats_panel.weighty = 0.2;
		panell_principal.add( panell_tanca_sessio, propietats_panel );
		propietats_panel.gridx = 2;
		propietats_panel.gridy = 2;
		propietats_panel.weightx = 0.25;
		propietats_panel.anchor = GridBagConstraints.SOUTHEAST;
		panell_principal.add( panell_sortida, propietats_panel );
		propietats_panel.gridx = 0;
		propietats_panel.gridy = 2;
		propietats_panel.weightx = 0.25;
		propietats_panel.anchor = GridBagConstraints.SOUTHWEST;
		panell_principal.add( Box.createHorizontalStrut( 65 ), propietats_panel );
	}

	@Override
	protected void inicialitzaPanellCentral()
	{
		panell_botons.setLayout( new GridLayout( 4, 1, 20, 20 ) );
		panell_botons.add( juga );
		if ( PresentacioCtrl.getInstancia().getEsConvidat() )
		{
			carrega.setEnabled( false );
		}
		panell_botons.add( carrega );
		panell_botons.add( configuracio );
		panell_botons.add( ranquing );
		panell_botons.setOpaque( false );
	}

	@Override
	protected void inicialitzaPanellPeu()
	{
		nom_jugador_principal =
				new JLabel( "Has iniciat sessió com a " + PresentacioCtrl.getInstancia().obteNomJugadorPrincipal() );

		panell_tanca_sessio.add( nom_jugador_principal );
		panell_tanca_sessio.add( tanca_sessio );
		panell_tanca_sessio.setOpaque( false );
	}

	@Override
	protected void assignaListeners()
	{
		super.assignaListeners();

		juga.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoJuga();
			}
		} );

		carrega.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoCarrega();
			}
		} );

		configuracio.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoPreferencies();
			}
		} );

		ranquing.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoRanquing();
			}
		} );

		tanca_sessio.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoTancaSessio();
			}
		} );
	}

	/**
	 * Defineix el comportament del botó de jugar una partida quan sigui pitjat.
	 */
	public void accioBotoJuga()
	{
		PresentacioCtrl.getInstancia().vistaMenuPrincipalAConfiguraPartida();
	}

	/**
	 * Defineix el comportament del botó de carregar una partida quan sigui pitjat.
	 */
	public void accioBotoCarrega()
	{
		PresentacioCtrl.getInstancia().vistaMenuPrincipalACarregaPartida();
	}

	/**
	 * Defineix el comportament del botó de preferències quan sigui pitjat.
	 */
	public void accioBotoPreferencies()
	{
		PresentacioCtrl.getInstancia().vistaMenuPrincipalAPreferencies();
	}

	/**
	 * Defineix el comportament del botó de rànquing quan sigui pitjat.
	 */
	public void accioBotoRanquing()
	{
		PresentacioCtrl.getInstancia().vistaMenuPrincipalARanquing();
	}

	/**
	 * Defineix el comportament del botó de tancar sessió quan sigui pitjat.
	 */
	public void accioBotoTancaSessio()
	{
		PresentacioCtrl.getInstancia().vistaMenuPrincipalAIniciaSessio();
	}
}
