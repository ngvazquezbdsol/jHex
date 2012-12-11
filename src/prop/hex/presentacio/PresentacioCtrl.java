package prop.hex.presentacio;

import prop.cluster.domini.models.estats.EstatCasella;
import prop.cluster.domini.models.estats.EstatPartida;
import prop.hex.domini.controladors.PartidaCtrl;
import prop.hex.domini.controladors.UsuariCtrl;
import prop.hex.domini.models.Casella;
import prop.hex.domini.models.Ranquing;
import prop.hex.domini.models.UsuariHex;
import prop.hex.domini.models.enums.CombinacionsColors;
import prop.hex.domini.models.enums.ModesInici;
import prop.hex.domini.models.enums.TipusJugadors;
import prop.hex.presentacio.auxiliars.VistaDialeg;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Controlador de presentació del joc Hex.
 * Inicialitza totes les estructures relacionades amb la interfície i s'encarrega de la comunicació entre les vistes
 * i la capa de domini.
 *
 * @author Guillermo Girona San Miguel (Grup 7.3, Hex)
 */

public final class PresentacioCtrl
{

	/**
	 * Instància del controlador.
	 */
	private static PresentacioCtrl instancia;

	/**
	 * Constructor per defecte. Declarat privat perquè és una classe singleton
	 */
	private PresentacioCtrl()
	{
	}

	/**
	 * Consultora de l'instancia actual del controlador de partida.
	 * Si encara no s'ha inicialitzat l'objecte, crida el constructor. Si ja s'ha instanciat prèviament,
	 * simplement retorna l'instància ja creada.
	 *
	 * @return L'objecte singleton amb el el controlador de presentació.
	 */
	public static synchronized PresentacioCtrl getInstancia()
	{
		if ( instancia == null )
		{
			instancia = new PresentacioCtrl();
		}

		return instancia;
	}

	/**
	 * Frame principal de la interfície del joc.
	 */
	private JFrame frame_principal = new JFrame( "jHex - Joc de Taula Hex" );

	/**
	 * Vista d'inici de sessió.
	 */
	private IniciaSessioVista inicia_sessio_vista = new IniciaSessioVista( frame_principal );

	/**
	 * Vista de registre d'un usuari.
	 */
	private RegistraVista registra_vista;

	/**
	 * Vista del menú principal.
	 */
	private MenuPrincipalVista menu_principal_vista;

	/**
	 * Vista de les preferències de l'usuari.
	 */
	private ConfiguracioVista preferencies_vista;

	/**
	 * Vista del menú de configuració d'una nova partida.
	 */
	private ConfiguraPartidaVista configura_partida_vista;

	/**
	 * Vista del rànquing.
	 */
	private RanquingVista ranquing_vista;

	/**
	 * Vista de la pantalla de carregar una partida guardada.
	 */
	private CarregaPartidaVista carrega_partida_vista;

	/**
	 * Vista de la pantalla d'identificar-se per a carregar una partida guardada.
	 */
	private IdentificaCarregaPartidaVista identifica_carrega_partida_vista;

	/**
	 * Vista de la pantalla de definició d'una situació inicial.
	 */
	private DefineixSituacioVista defineix_situacio_vista;

	/**
	 * Vista del visualitzador de partides.
	 */
	private PartidaVista partida_vista;

	/**
	 * Vista del menú de canvi de la contrasenya.
	 */
	private CanviaContrasenyaVista canvia_contrasenya_vista;

	/**
	 * Inicialitza el controlador de presentació, establint la mida de la finestra, la icona, i la primera vista a mostrar.
	 */
	public void inicialitzaPresentacio()
	{
		// Mida fixa i sense opció a redimensionar per evitar que hi hagi errors en el posicionament dels botons.
		frame_principal.setMinimumSize( new Dimension( 800, 600 ) );
		frame_principal.setPreferredSize( frame_principal.getMinimumSize() );
		frame_principal.setResizable( false );
		frame_principal.setLocationRelativeTo( null );
		frame_principal.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame_principal.setIconImage( new ImageIcon( "img/logo-36_36.png" ).getImage() ); // Icona finestra windows
		try // Icona dock Mac
		{
			Method metode = Class.forName( "com.apple.eawt.Application" ).getMethod( "getApplication" );
			Object aplicacio = metode.invoke( null );
			aplicacio.getClass().getMethod( "setDockIconImage", Image.class )
					.invoke( aplicacio, new ImageIcon( "img/logo-120_120.png" ).getImage() );
		}
		catch ( Exception excepcio )
		{
			// no estem a Mac OS X.
		}

		try
		{
			PartidaCtrl.comprovaConsistenciaFitxersIDades();
		}
		catch ( Exception e )
		{
			VistaDialeg dialeg = new VistaDialeg();
			String[] botons = { "Accepta" };
			String valor_seleccionat = dialeg.setDialeg( "Error", "Error inicialitzant les dades del joc.", botons,
					JOptionPane.ERROR_MESSAGE );

			e.printStackTrace(); // Imprimeixo l'error per consola per poder-ho debugar
		}

		inicia_sessio_vista.fesVisible();
	}

	/**
	 * Estableix l'usuari identificat al joc com l'usuari corresponent al nom i la contrasenya que passem com a
	 * paràmetre.
	 *
	 * @param nom         Nom de l'usuari que es vol carregar.
	 * @param contrasenya Contrasenya de l'usuari que es vol carregar.
	 * @return Cert, si l'usuari es carrega correctament. Fals, altrament.
	 * @throws IllegalArgumentException Si l'usuari identificat pel nom no existeix
	 *                                  i, si es vol carregar un jugador, si la contrasenya no coincideix amb
	 *                                  l'usuari.
	 * @throws FileNotFoundException    Si el fitxer no s'ha generat i no s'han pogut escriure les dades.
	 * @throws IOException              IOException Si ha succeït un error d'entrada/sortida inesperat.
	 * @throws ClassNotFoundException   Si hi ha un problema de classes quan es carrega l'usuari.
	 * @throws NullPointerException     Es dona si el fitxer està buit.
	 */
	public void setUsuariActual( String nom, String contrasenya )
			throws IllegalArgumentException, FileNotFoundException, IOException, ClassNotFoundException,
			       NullPointerException
	{
		UsuariCtrl.getInstancia().setUsuariPrincipal( nom, contrasenya );
	}

	/**
	 * Indica si l'usuari identificat al joc és un convidat.
	 *
	 * @return Cert, si l'usuari identificat al joc és un convidat. Fals, altrament.
	 */
	public boolean getEsConvidat()
	{
		return UsuariCtrl.getInstancia().esConvidat();
	}

	/**
	 * Identifica un usuari convidat al sistema.
	 */
	public void entraConvidat()
	{
		UsuariCtrl.getInstancia().entraConvidat();
	}

	/**
	 * Registra un usuari amb el nom d'usuari i la contrasenya donats als sistema, guardant les seves dades a memòria
	 * principal i afegint-lo al rànquing.
	 *
	 * @param nom           Nom de l'usuari nou que es vol registrar al sistema.
	 * @param contrasenya   Contrasenya de l'usuari nou que es vol registrar al sistema.
	 * @throws IllegalArgumentException Si el nom d'usuari ja existeix al sistema,
	 *                                  si conté caràcters il·legals o si es tracta d'un nom no permès.
	 * @throws IOException              Si ha succeït un error d'entrada/sortida inesperat.
	 */
	public void registraUsuari( String nom, String contrasenya ) throws IllegalArgumentException, IOException
	{
		UsuariCtrl.getInstancia().creaUsuari( nom, contrasenya, TipusJugadors.JUGADOR );
	}

	/**
	 * Guarda les dades de l'usuari identificat al sistema en memòria secundària.
	 *
	 * @throws IOException           Si ha succeït un error d'entrada/sortida inesperat.
	 * @throws FileNotFoundException Si el fitxer no s'ha generat i no s'han pogut escriure les dades.
	 */
	public void guardaJugadorPrincipal() throws IOException, FileNotFoundException
	{
		if ( !getEsConvidat() )
		{
			UsuariCtrl.getInstancia().guardaUsuari();
		}
	}

	/**
	 * Obté el nom de l'usuari identificat al sistema.
	 *
	 * @return
	 */
	public String obteNomJugadorPrincipal()
	{
		return UsuariCtrl.getInstancia().obteNom();
	}

	// Mètodes ConfiguracioVista
	// ----------------------------------------------------------------------------------------------------------------

	public ModesInici obteModeIniciJugadorPrincipal()
	{
		return UsuariCtrl.getInstancia().obteModeInici();
	}

	public CombinacionsColors obteCombinacioDeColorsJugadorPrincipal()
	{
		return UsuariCtrl.getInstancia().obteCombinacioColors();
	}

	public void modificaPreferenciesJugadorPrincipal( ModesInici mode_inici, CombinacionsColors combinacio_colors )
	{
		UsuariCtrl.getInstancia().modificaPreferencies( mode_inici, combinacio_colors );
	}

	public void reiniciaEstadistiquesJugadorPrincipal()
	{
		UsuariCtrl.getInstancia().reiniciaEstadistiques();
	}

	public void canviaContrasenyaJugadorPrincipal( String contrasenya_actual, String contrasenya_nova )
			throws IllegalArgumentException
	{
		UsuariCtrl.getInstancia().modificaContrasenya( contrasenya_actual, contrasenya_nova );
	}

	public void eliminaUsuariJugadorPrincipal()
	{
		UsuariCtrl.getInstancia().eliminaUsuariJugadorPrincipal();
	}

	// Mètodes ConfiguraPartidaVista
	// ----------------------------------------------------------------------------------------------------------------

	public void preInicialitzaUsuariPartida( int num_jugador, TipusJugadors tipus_jugador, String nom_usuari,
	                                         String contrasenya_usuari )
			throws IllegalArgumentException, FileNotFoundException, IOException, ClassNotFoundException,
			       NullPointerException
	{
		PartidaCtrl.getInstancia()
				.preInicialitzaUsuariPartida( num_jugador, tipus_jugador, nom_usuari, contrasenya_usuari );
	}

	public void inicialitzaPartida( int mida_tauler, String nom_partida, boolean situacio_inicial )
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException
	{
		PartidaCtrl.getInstancia().inicialitzaPartida( mida_tauler, nom_partida, situacio_inicial );
	}

	public boolean existeixUsuari( String nom )
	{
		return UsuariCtrl.getInstancia().existeixUsuari( nom );
	}

	// Mètodes RanquingVista
	// ----------------------------------------------------------------------------------------------------------------

	public String[][] getClassificacioFormatejada()
	{
		List<UsuariHex> classificacio = Ranquing.getInstancia().getClassificacio();

		String[][] classificacio_formatejada = new String[classificacio.size()][4];

		int i = 0;
		for ( UsuariHex usuari_classificat : classificacio )
		{
			classificacio_formatejada[i][0] = usuari_classificat.getNom();

			Integer partides_jugades = usuari_classificat.getPartidesJugades();
			classificacio_formatejada[i][1] = partides_jugades.toString();

			// Càlcul del percentatge de victòries
			Float percentatge_victories = new Float( ( usuari_classificat.getPartidesGuanyades().floatValue() /
			                                     partides_jugades.floatValue() ) * 100 );
			// Redondeig a dos decimals
			percentatge_victories = ( float ) ( Math.round( percentatge_victories * 100.0 ) / 100.0 );

			// Per poder ordenar :)
			if ( percentatge_victories > 99.99 )
			{
				percentatge_victories = ( float ) 99.99;
			}

			classificacio_formatejada[i][2] = percentatge_victories.toString() + "%";
			classificacio_formatejada[i][3] = usuari_classificat.getPuntuacioGlobal().toString();
			i++;
		}

		return classificacio_formatejada;
	}

	public String[][] getHallOfFameFormatejat()
	{
		Ranquing ranquing = Ranquing.getInstancia();

		String[][] hall_of_fame_formatejat = new String[4][3];

		hall_of_fame_formatejat[0] = getFitaHallOfFameFormatejada(
				"Victòria amb menys fitxes",
				ranquing.getUsuariFitxesMinimes(),
				ranquing.getFitxesMinimes().toString()
		);

		hall_of_fame_formatejat[1] = getFitaHallOfFameFormatejada(
				"Més victòries",
				ranquing.getUsuariMesPartidesGuanyades(),
				ranquing.getMesPartidesGuanyades().toString()
		);

		hall_of_fame_formatejat[2] = getFitaHallOfFameFormatejada(
				"Més partides jugades",
				ranquing.getUsuariMesPartidesJugades(),
				ranquing.getMesPartidesJugades().toString()
		);

		hall_of_fame_formatejat[3] = getFitaHallOfFameFormatejada(
				"Victòria en menys temps",
				ranquing.getUsuariTempsMinim(),
				String.valueOf( ranquing.getTempsMinim() / 1000L ) + "." +
				( ranquing.getTempsMinim() % 1000L ) / 100 + " segs."
		);

		return hall_of_fame_formatejat;
	}

	private String[] getFitaHallOfFameFormatejada( String nom_fita, String nom_usuari, String record )
	{
		String[] dades_fita = new String[3];

		dades_fita[0] = nom_fita;

		if ( nom_usuari != null )
		{
			dades_fita[1] = nom_usuari;
			dades_fita[2] = record;
		}
		else
		{
			dades_fita[1] = "-";
			dades_fita[2] = "(Per aconseguir)";
		}

		return dades_fita;
	}

	// Mètodes CarregaPartidaVista
	// ----------------------------------------------------------------------------------------------------------------

	public String[][] obteLlistaPartides()
	{
		return PartidaCtrl.getInstancia().llistaPartidesUsuari();
	}

	public String usuariSenseAutenticarAPartida( String id_partida ) throws IOException, ClassNotFoundException
	{
		return PartidaCtrl.getInstancia().usuariSenseAutenticarAPartida( id_partida );
	}

	public void carregaPartida( String id_partida, String contrasenya_contrincant )
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		PartidaCtrl.getInstancia().carregaPartida( id_partida, contrasenya_contrincant );
	}

	public void eliminaPartida( String id_partida )
	{
		PartidaCtrl.getInstancia().eliminaPartida( id_partida );
	}

	// Metodes JPanelTauler
	// ----------------------------------------------------------------------------------------------------------------

	public EstatPartida consultaEstatPartida()
	{
		return PartidaCtrl.getInstancia().consultaEstatPartida();
	}

	public void mouFitxa( int fila, int columna ) throws UnsupportedOperationException
	{
		PartidaCtrl.getInstancia().mouFitxa( fila, columna );
	}

	public EstatCasella getEstatCasella( int fila, int columna )
	{
		return PartidaCtrl.getInstancia().getEstatCasella( fila, columna );
	}

	public void executaMovimentIA()
	{
		PartidaCtrl.getInstancia().executaMovimentIA();
	}

	public boolean esTornHuma()
	{
		return PartidaCtrl.getInstancia().esTornHuma();
	}

	public boolean esCasellaCentral( int fila, int columna )
	{
		return PartidaCtrl.getInstancia().esCasellaCentral( fila, columna );
	}

	public Casella obtePista()
	{
		return PartidaCtrl.getInstancia().obtePista();
	}

	public Object[] getElementsDeControlPartida()
	{
		return PartidaCtrl.getInstancia().getElementsDeControlPartida();
	}

	public Object[][] getElementsDeControlJugadors()
	{
		return PartidaCtrl.getInstancia().getElementsDeControlJugadors();
	}
    
    public void mostraDialegVictoriaPartida( String missatge )
    {
        partida_vista.mostraDialegVictoria( missatge );
    }
    
    public void mostraDialegVictoriaDefineixSituacio( String missatge )
    {
        defineix_situacio_vista.mostraDialegVictoria( missatge );
    }

	// Mètodes PartidaVista
	// ----------------------------------------------------------------------------------------------------------------

	public void estatBotoIntercanviaFitxa( boolean estat )
	{
		partida_vista.estatBotoIntercanviaFitxa( estat );
	}

	public void estatBotoDemanaPista( boolean estat )
	{
		partida_vista.estatBotoDemanaPista( estat );
	}

	public void intercanviaFitxa()
	{
		PartidaCtrl.getInstancia().intercanviaDarreraFitxa();
	}

	public void finalitzaPartida() throws IOException
	{
		PartidaCtrl.getInstancia().finalitzaPartida();
	}

	public void guardaPartida() throws IOException, UnsupportedOperationException
	{
		PartidaCtrl.getInstancia().guardaPartida();
	}

	public void netejaParametresPartidaActual()
	{
		PartidaCtrl.getInstancia().netejaParametresPartidaActual();
	}

	// Mètodes per intercanviar vistes
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Canvia la vista d'iniciar sessió per la de registrar-se.
	 */
	public void vistaIniciaSessioARegistra()
	{
		if ( registra_vista == null )
		{
			registra_vista = new RegistraVista( frame_principal );
		}
		inicia_sessio_vista = null;
		registra_vista.fesVisible();
	}

	/**
	 * Canvia la vista de registrar-se per la d'iniciar sessió.
	 */
	public void vistaRegistraAIniciaSessio()
	{
		if ( inicia_sessio_vista == null )
		{
			inicia_sessio_vista = new IniciaSessioVista( frame_principal );
		}
		registra_vista = null;
		inicia_sessio_vista.fesVisible();
	}

	/**
	 * Canvia la vista de registrar-se per la del menú principal.
	 */
	public void vistaRegistraAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		registra_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista d'iniciar sessió per la del menú principal.
	 */
	public void vistaIniciaSessioAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}

		inicia_sessio_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista del menú principal per la d'iniciar sessió.
	 */
	public void vistaMenuPrincipalAIniciaSessio()
	{
		if ( inicia_sessio_vista == null )
		{
			inicia_sessio_vista = new IniciaSessioVista( frame_principal );
		}
		menu_principal_vista = null;
		inicia_sessio_vista.fesVisible();
	}

	/**
	 * Canvia la vista del menú principal per la de modificar les preferències.
	 */
	public void vistaMenuPrincipalAPreferencies()
	{
		if ( preferencies_vista == null )
		{
			preferencies_vista = new ConfiguracioVista( frame_principal );
		}
		menu_principal_vista = null;
		preferencies_vista.fesVisible();
	}

	/**
	 * Canvia la vista de modificar les preferències per la del menú principal.
	 */
	public void vistaPreferenciesAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		preferencies_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista de canviar la contrasenya per la de modificar les preferències.
	 */
	public void vistaCanviaContrasenyaAPreferencies()
	{
		if ( preferencies_vista == null )
		{
			preferencies_vista = new ConfiguracioVista( frame_principal );
		}
		canvia_contrasenya_vista = null;
		preferencies_vista.fesVisible();
	}

	/**
	 * Canvia la vista de canviar la contrasenya per la del menú principal.
	 */
	public void vistaCanviaContrasenyaAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		canvia_contrasenya_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista de modificar les preferències per la de canviar la contrasenya.
	 */
	public void vistaPreferenciesACanviaContrasenya()
	{
		if ( canvia_contrasenya_vista == null )
		{
			canvia_contrasenya_vista = new CanviaContrasenyaVista( frame_principal );
		}
		preferencies_vista = null;
		canvia_contrasenya_vista.fesVisible();
	}

	/**
	 * Canvia la vista del menú principal per la de configurar la partida.
	 */
	public void vistaMenuPrincipalAConfiguraPartida()
	{
		if ( configura_partida_vista == null )
		{
			configura_partida_vista = new ConfiguraPartidaVista( frame_principal );
		}
		menu_principal_vista = null;
		configura_partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de configurar la partida per la del menú principal.
	 */
	public void vistaConfiguraPartidaAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		configura_partida_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista del menú principal per la del rànquing.
	 */
	public void vistaMenuPrincipalARanquing()
	{
		if ( ranquing_vista == null )
		{
			ranquing_vista = new RanquingVista( frame_principal );
		}
		menu_principal_vista = null;
		ranquing_vista.fesVisible();
	}

	/**
	 * Canvia la vista del rànquing per la del menú principal.
	 */
	public void vistaRanquingAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		ranquing_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista del menú principal per la de carregar partida.
	 */
	public void vistaMenuPrincipalACarregaPartida()
	{
		if ( carrega_partida_vista == null )
		{
			carrega_partida_vista = new CarregaPartidaVista( frame_principal );
		}
		menu_principal_vista = null;
		carrega_partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de carregar partida per la del menú principal.
	 */
	public void vistaCarregaPartidaAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		carrega_partida_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista de carregar partida per la del visualitzador de partides.
	 */
	public void vistaCarregaPartidaAPartida()
	{
		if ( partida_vista == null )
		{
			partida_vista = new PartidaVista( frame_principal );
		}
		carrega_partida_vista = null;
		partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de carregar partida per la de identificar-se per a carregar una partida.
	 *
	 * @param usuari Usuari que s'ha d'identificar.
	 * @param id_partida Identificador de la partida que es vol carregar.
	 */
	public void vistaCarregaPartidaAIdentificaCarregaPartida( String usuari, String id_partida )
	{
		if ( identifica_carrega_partida_vista == null )
		{
			identifica_carrega_partida_vista =
					new IdentificaCarregaPartidaVista( frame_principal, usuari, id_partida );
		}
		carrega_partida_vista = null;
		identifica_carrega_partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista d'identificar-se per a carregar una partida per la del visualitzador de partides.
	 */
	public void vistaIdentificaCarregaPartidaAPartida()
	{
		if ( partida_vista == null )
		{
			partida_vista = new PartidaVista( frame_principal );
		}
		identifica_carrega_partida_vista = null;
		partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista d'identificar-se per a carregar una partida per la del menú principal.
	 */
	public void vistaIdentificaCarregaPartidaAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		identifica_carrega_partida_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista d'identificar-se per a carregar una partida per la de carregar una partida.
	 */
	public void vistaIdentificaCarregaPartidaACarregaPartida()
	{
		if ( carrega_partida_vista == null )
		{
			carrega_partida_vista = new CarregaPartidaVista( frame_principal );
		}
		identifica_carrega_partida_vista = null;
		carrega_partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de configurar una partida per la del visualitzador de partides.
	 */
	public void vistaConfiguraPartidaAPartida()
	{
		if ( partida_vista == null )
		{
			partida_vista = new PartidaVista( frame_principal );
		}
		configura_partida_vista = null;
		partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de configurar una partida per la de definir una situació inicial.
	 */
	public void vistaConfiguraPartidaADefineixSituacio()
	{
		if ( defineix_situacio_vista == null )
		{
			defineix_situacio_vista = new DefineixSituacioVista( frame_principal );
		}
		configura_partida_vista = null;
		defineix_situacio_vista.fesVisible();
	}

	/**
	 * Canvia la vista del visualitzador de partides per la del menú principal.
	 */
	public void vistaPartidaAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		partida_vista = null;
		menu_principal_vista.fesVisible();
	}

	/**
	 * Canvia la vista de definir una situació inicial per la del visualitzador de partides.
	 */
	public void vistaDefineixSituacioAPartida()
	{
		if ( partida_vista == null )
		{
			partida_vista = new PartidaVista( frame_principal );
		}
		defineix_situacio_vista = null;

		PartidaCtrl.getInstancia().acabaDeDefinirSituacioInicial();
		partida_vista.fesVisible();
	}

	/**
	 * Canvia la vista de definir una situació inicial per la del menú principal.
	 */
	public void vistaDefineixSituacioAMenuPrincipal()
	{
		if ( menu_principal_vista == null )
		{
			menu_principal_vista = new MenuPrincipalVista( frame_principal );
		}
		defineix_situacio_vista = null;
		menu_principal_vista.fesVisible();
	}
}
