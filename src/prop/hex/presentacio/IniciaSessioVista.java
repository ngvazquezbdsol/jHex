package prop.hex.presentacio;

import prop.hex.presentacio.auxiliars.JPanelImatge;
import prop.hex.presentacio.auxiliars.VistaDialeg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Vista d'iniciar sessió del joc Hex.
 *
 * @author Guillermo Girona San Miguel (Grup 7.3, Hex)
 */
public final class IniciaSessioVista extends BaseVista
{

	// Panells

	/**
	 * Panell central de la vista. Conté les dades de l'usuari.
	 */
	private JPanel panell_dades;

	/**
	 * Panell que conté els botons de la vista.
	 */
	private JPanel panell_botons;

	// Botons

	/**
	 * Botó d'accepta.
	 */
	private JButton accepta;

	/**
	 * Botó de registra't.
	 */
	private JButton registra;

	/**
	 * Botó d'entra com a convidat.
	 */
	private JButton convidat;

	// Camps de tipus text/contrasenya

	/**
	 * Camp de text del nom d'usuari.
	 */
	private JTextField usuari;

	/**
	 * Camp de la contrasenya.
	 */
	private JPasswordField contrasenya;

	// Etiquetes de text

	/**
	 * Etiqueta de nom d'usuari.
	 */
	private JLabel text_usuari;

	/**
	 * Etiqueta de contrasenya.
	 */
	private JLabel text_contrasenya;

	/**
	 * Constructor que crea una vista passant-li quin és el frame sobre el qual s'haurà de treballar.
	 *
	 * @param frame_principal Frame principal sobre el que s'hauran d'afegir els diferents components.
	 */
	public IniciaSessioVista( JFrame frame_principal )
	{
		super( frame_principal );

		titol = new JLabel( "Inicia sessió" );
		panell_dades = new JPanelImatge( getClass().getResource( "/prop/img/caixa.png" ) );
		panell_botons = new JPanel();
		accepta = new JButton( "Accepta" );
		registra = new JButton( "Registra't" );
		convidat = new JButton( "Entra com a convidat" );
		usuari = new JTextField();
		contrasenya = new JPasswordField();
		text_usuari = new JLabel( "Nom d'usuari:" );
		text_contrasenya = new JLabel( "Contrasenya:" );

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
		propietats_panel.weighty = 0.1;
		panell_principal.add( panell_titol, propietats_panel );
		propietats_panel.gridx = 1;
		propietats_panel.gridy = 1;
		propietats_panel.weighty = 0.5;
		panell_principal.add( panell_dades, propietats_panel );
		propietats_panel.gridx = 1;
		propietats_panel.gridy = 2;
		propietats_panel.weighty = 0.4;
		panell_principal.add( panell_botons, propietats_panel );
		propietats_panel.fill = GridBagConstraints.NONE;
		propietats_panel.gridx = 2;
		propietats_panel.gridy = 2;
		propietats_panel.weightx = 0.25;
		propietats_panel.weighty = 0.1;
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
		panell_dades.setBorder( BorderFactory.createRaisedBevelBorder() );
		panell_dades.setLayout( new BoxLayout( panell_dades, BoxLayout.PAGE_AXIS ) );
		JPanel panell_camps = new JPanel();
		panell_camps.setLayout( new GridLayout( 2, 2, 10, 10 ) );
		panell_camps.add( text_usuari );
		panell_camps.add( usuari );
		panell_camps.add( text_contrasenya );
		panell_camps.add( contrasenya );
		panell_camps.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		panell_camps.setOpaque( false );
		panell_dades.add( panell_camps );
		accepta.setAlignmentX( Component.CENTER_ALIGNMENT );
		panell_dades.add( accepta );
		panell_dades.add( Box.createVerticalStrut( 10 ) );
	}

	@Override
	protected void inicialitzaPanellPeu()
	{
		panell_botons.setLayout( new BoxLayout( panell_botons, BoxLayout.PAGE_AXIS ) );
		registra.setAlignmentX( Component.CENTER_ALIGNMENT );
		panell_botons.add( registra );
		panell_botons.add( Box.createVerticalStrut( 10 ) );
		convidat.setAlignmentX( Component.CENTER_ALIGNMENT );
		panell_botons.add( convidat );
		panell_botons.setOpaque( false );
	}

	@Override
	protected void assignaListeners()
	{
		super.assignaListeners();

		accepta.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoAccepta();
			}
		} );

		convidat.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoConvidat();
			}
		} );

		registra.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent event )
			{
				accioBotoRegistra();
			}
		} );
	}

	/**
	 * Defineix el comportament del botó d'acceptar quan sigui pitjat.
	 */
	public void accioBotoAccepta()
	{
		try
		{
			PresentacioCtrl.getInstancia().setUsuariActual( usuari.getText(), new String( contrasenya.getPassword() ) );
			PresentacioCtrl.getInstancia().vistaIniciaSessioAMenuPrincipal();
		}
		catch ( IllegalArgumentException excepcio )
		{
			VistaDialeg dialeg = new VistaDialeg();
			String[] botons = { "Accepta" };
			String valor_seleccionat = dialeg.setDialeg( "Error", "Nom d'usuari o contrasenya incorrectes.", botons,
					JOptionPane.WARNING_MESSAGE );
		}
		catch ( Exception excepcio )
		{
			VistaDialeg dialeg = new VistaDialeg();
			String[] botons = { "Accepta" };
			String valor_seleccionat =
					dialeg.setDialeg( "Error", "Error inesperat.", botons, JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Defineix el comportament del botó d'entrar com a convidat quan sigui pitjat.
	 */
	public void accioBotoConvidat()
	{
		try
		{
			PresentacioCtrl.getInstancia().entraConvidat();
			PresentacioCtrl.getInstancia().vistaIniciaSessioAMenuPrincipal();
		}
		catch ( IllegalArgumentException excepcio )
		{
			VistaDialeg dialeg = new VistaDialeg();
			String[] botons = { "Accepta" };
			String valor_seleccionat = dialeg.setDialeg( "Error", "Nom d'usuari o contrasenya incorrectes.", botons,
					JOptionPane.WARNING_MESSAGE );
		}
		catch ( Exception excepcio )
		{
			VistaDialeg dialeg = new VistaDialeg();
			String[] botons = { "Accepta" };
			String valor_seleccionat =
					dialeg.setDialeg( "Error", "Error inesperat.", botons, JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Defineix el comportament del botó de registrar-se quan sigui pitjat.
	 */
	public void accioBotoRegistra()
	{
		PresentacioCtrl.getInstancia().vistaIniciaSessioARegistra();
	}
}
