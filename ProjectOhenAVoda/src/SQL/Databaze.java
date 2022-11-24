package SQL;

import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import EnumInteface.ObjectMapy;

public class Databaze {

	/*
	(	CREATE TABLE test(SouradniceX int ,
			SouradniceY int,
			VelikostHeight int,
			VelikostWidth int,
			ImageUrl varchar(80),
			Prostupnost int,
			UniCode int PRIMARY  KEY AUTO_INCREMENT )
	*/
	
	private final static String CreateDatabaze=new String ("CREATE TABLE %s (SouradniceX int ,"+
			"SouradniceY int,"+
			"VelikostHeight int,"+
			"VelikostWidth int,"+
			"ImageUrl varchar(80),"+
			"Prostupnost int,"+
			"UniCode int PRIMARY  KEY AUTO_INCREMENT )");
	
	private final static String nameOfDatabaze="fireandwomen";
	private final static String SouradniceX ="SouradniceX";
	private final static String SouradniceY ="SouradniceY";
	private final static String VelikostHeight ="VelikostHeight";
	private final static String VelikostWidth="VelikostWidth";
	private final static String ImageUrl="ImageUrl";
	private final static String Prostupnost="Prostupnost";
	private final static String Unicode="UniCode";
	
	
	private final static String SpatneJmenoMapy="dassad";
	private static  Connection connection;
	 
	 private static void setConnection(Connection con) {
		Databaze.connection=con; 
	 }
	 
	 
	 private static Connection getConnection() {
		 return connection;
	 }
	
	public Databaze() {
	
		try {
		Class.forName("com.mysql.jdbc.Driver");  
		this.setConnection(DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/"+this.nameOfDatabaze,"root",""));
	
		}
		catch(Exception e) {
		Databaze.SendErrorMessage("Chyba v connection    "+e, true);	
		
		}
		
		
	
	
	public synchronized void SmazZaznam(String nameOfMap,int UniCode) {
		
		Statement stm;
		ResultSet rs;
		try {
			stm=getConnection().createStatement();
			rs=stm.executeQuery(String.format("Delete from%s where %s= \"%d\"", nameOfMap,Databaze.Unicode,UniCode));
			
		
		}
		catch(Exception e) {
			
		}
		
	}
	
	
	public static void NovaMapa(String nameOfMap) {
		
		try {
			getConnection().createStatement().execute(String.format(Databaze.CreateDatabaze,nameOfMap));
			

		}
		catch(java.sql.SQLSyntaxErrorException ee) {
			if(ee.getMessage().equals("Table 'test' already exists")) {
				Databaze.SendErrorMessage("Mapa s nazvem "+nameOfMap+" jiz existuje, zvol jine jmeno", false);
			}
			else {
				Databaze.SendErrorMessage("Chyba pri vytvareni nove Tabulky, jmeno tabulky:   "+nameOfMap, true);
				Databaze.SendErrorMessage(ee.toString(), true);
			}
		}
		catch(Exception e) {
			Databaze.SendErrorMessage("Chyba pri vytvareni nove Tabulky, jmeno tabulky:   "+nameOfMap, true);
			Databaze.SendErrorMessage(e.toString(), true);
		}
	}
	
	public static synchronized void UlozObjectMapy(String nameOfMap,ObjectMapy obj) {
		try {
		Databaze.UlozObjectMapy(nameOfMap, obj.getUniCode(), obj.getLocation().x, obj.getLocation().y, obj.getSize().height, obj.getSize().width, obj.getImageUrl(), obj.getProstupnost());
		
		}
		catch(Exception e) {
			Databaze.SendErrorMessage("Chyba v ukladani Objectu mapy-pouzeObject    "+e, true);
		}
	}
	
	public static void UlozBacgroundImageMapy(String nameOfMap, int UniCode,String imageUrl) {
	
		Databaze.UlozObjectMapy(nameOfMap, UniCode,0, 0, 0, 0, imageUrl, 0);
	}
	
	public static synchronized void UlozObjectMapy(String nameOfMap,int UniCode,int SouradniceX,int SouradniceY,int VelikostHeight,int VelikostWidth,String ImageUrl,int prostupnost) {
	
		
		Statement stm;
		ResultSet rs;
		try {
			stm=getConnection().createStatement();
			switch(UniCode) {
		
			case 0:
				String parametr=String.format("Insert into %s (%s,%s,%s,%s,%s,%s,%s)", nameOfMap,Databaze.Unicode,Databaze.SouradniceX,Databaze.SouradniceY,Databaze.VelikostHeight,Databaze.VelikostWidth,Databaze.ImageUrl,Databaze.Prostupnost);
				
				String values=String.format(
				"values(%d,%d,%d,%d,%d,\"%s\",%d) ",  UniCode,SouradniceX, SouradniceY,VelikostHeight,VelikostWidth,ImageUrl,prostupnost);

				System.out.println(ImageUrl);
				stm.execute(parametr+values);
				
				break;
		
			default:
				String parametrs=String.format("Update %s set ",nameOfMap );
				String value=String.format("%s= %d, %s=%d, %s=%d, %s=%d, %s=\"%s\", %s= %d where %s= %d", 
					Databaze.SouradniceX,SouradniceX,
					Databaze.SouradniceY,SouradniceY,
					Databaze.VelikostHeight,VelikostHeight,
					Databaze.VelikostWidth,VelikostWidth,
					Databaze.ImageUrl,ImageUrl,
					Databaze.Prostupnost,prostupnost,
					Databaze.Unicode,UniCode);
				
				
				System.out.println(parametrs+value);
				stm.execute(parametrs+value);
			}
		}
		catch(Exception e) {
			Databaze.SendErrorMessage("Chyba v ukladani(ulozObjectMapy) mapy:     "+nameOfMap, true);
			Databaze.SendErrorMessage(e.toString(), true);
		}
		
	}
	

	public static Image getBacgroundImage(String nameOfMap) {
		
		
		Statement stmt=null;
		Image img=null;
		try {
		Class.forName("com.mysql.jdbc.Driver"); 
		setConnection(DriverManager.getConnection(String.format(
				"jdbc:mysql://localhost:3306/ %s",nameOfDatabaze,"root","")));

		stmt=getConnection().createStatement();  
		
		ResultSet rss=stmt.executeQuery(String.format("select * from %s where %s =\"\" and %s =0 and %s=0 and %s=0 and %s=0",nameOfMap,Databaze.Prostupnost,Databaze.SouradniceX,Databaze.SouradniceY,Databaze.VelikostHeight,Databaze.VelikostWidth));
		
		
		String url=rss.getString(Databaze.ImageUrl);
		if(url!=null) {
			img=ImageIO.read(new File(url));
		}
		
		
		
		
		}
		catch(java.sql.SQLSyntaxErrorException e) {
			if(e.getMessage().equals(String.format(String.format("Table '%s' doesn't exist",nameOfMap)))) {
			Databaze.SendErrorMessage(String.format("Table '%s' doesn't exist",nameOfMap), false);
			}
			else {
				Databaze.SendErrorMessage("Chyba v DatabazeSQL nacitani ImageBacgtound"+e, true);
			}
		}
		catch(Exception e ) {
			Databaze.SendErrorMessage("Chyba v DatabazeSQL nacitani ImageBacgtound"+e, true);
		}
		finally{
			try {
				stmt.close();
				return img;
			}
			catch(Exception e) {
				Databaze.SendErrorMessage("Chyba V databaSQL v uzavirani"+e, true);
				
			}	
		}
		return null;
	}
	
	
	public synchronized static ArrayList<ObjectMapy> NactiMapu(String nameOfMap,boolean DevelopMode){
			
		ArrayList<ObjectMapy> objMapy=new ArrayList<>();

		Statement stm=null;
		try {
		
			stm=getConnection().createStatement();  
			
			ResultSet rs=stm.executeQuery(String.format("select * from %s",nameOfMap));

			
		
			while(rs.next()) {
			

				objMapy.add(new ObjectMapy(rs.getInt(Databaze.Unicode),rs.getInt("SouradniceX"),rs.getInt("SouradniceY"),rs.getInt("VelikostHeight"),rs.getInt("VelikostWidth"),rs.getString("ImageUrl"),rs.getInt("prostupnost")));
			}
		

		}
		catch(java.sql.SQLSyntaxErrorException e) {
			if(DevelopMode==true) {
				Databaze.NovaMapa(nameOfMap);
			}
			else {
				if(e.getMessage().equals(String.format(String.format("Table '%s' doesn't exist",nameOfMap)))) {
					Databaze.SendErrorMessage(String.format("Table '%s' doesn't exist",nameOfMap), false);
				}
				else {
					Databaze.SendErrorMessage("Chyba v DatabazeSQL nacitani tabulky"+e, true);
				}
			
			}
		}
		catch(Exception e) {
			Databaze.SendErrorMessage("SQL nacistTabulku"+e, true);
		}
		finally{
			try {
				stm.close();
				return objMapy;
			}
			catch(Exception e) {
				Databaze.SendErrorMessage("Chyba V databaSQL v uzavirani"+e, true);
				
			}	
		}
		
		
		return null;
	}
	
	

	
	private static void SendErrorMessage(String errorMessage,boolean pouzeLogovat) {
		
		System.out.println(errorMessage);
	}
}
