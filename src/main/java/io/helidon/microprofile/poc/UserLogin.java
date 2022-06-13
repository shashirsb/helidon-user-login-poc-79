package io.helidon.microprofile.poc;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.helidon.common.http.SetCookie;
import io.helidon.microprofile.cors.CrossOrigin;


@Path("/user")
@ApplicationScoped
public class UserLogin {
	 
	 private final DataSource dataSource;
	 private static final Logger LOGGER = Logger.getLogger(UserLogin.class.getName());
	 private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());
	 @Inject
	  public UserLogin(@Named("orders") DataSource dataSource) throws SQLException {
	    super();
	    this.dataSource = Objects.requireNonNull(dataSource);
	    this.dataSource.setLoginTimeout(2000000);
	  }
	 
	 
	 @Path("/login")
	 @GET
	 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 @Produces(MediaType.APPLICATION_JSON)
	 public JsonObject userLogin(@QueryParam("email") String email,@QueryParam("password") String pass) throws SQLException{
		 LOGGER.info("DBOperations:userLogin"+email+pass);
		 String sqlQ = "select USER_ID, EMAIL, USER_PASSWORD from IKM_USER where email = ? and USER_PASSWORD = ?";
		 String insertQuery = "INSERT INTO USER_SSO (SSO_ID, SESSION_ID, LOGIN_STATUS, LOGIN_TIME, USER_ID,USER_EMAIL) VALUES (LOGIN_SEQ.nextVal,?,?,?,?,?)";
		 JsonObject jb = null;
		 Response returnValue = null;
		 Connection connection = null;
		 try {
			 connection = this.dataSource.getConnection();
			
			PreparedStatement statement = connection.prepareStatement(sqlQ);
				 statement.setString(1, email);
				 statement.setString(2, pass);
				 
			ResultSet rs = statement.executeQuery();
			
			int userId = 0;
			String emailId = null;
			
			while(rs.next()) {
				userId = rs.getInt("USER_ID");
				emailId = rs.getString("EMAIL");
			}
			LOGGER.info("User _ID ->"+userId);
			LOGGER.info("emailId _ID ->"+emailId);
			
			 if(userId != 0 && emailId !=null){
				 
				 System.out.println("Where resultset is available");
				 UUID uuid = UUID.randomUUID();
				 PreparedStatement stmt = connection.prepareStatement(insertQuery);
				 long now = System.currentTimeMillis();
				 stmt.setString(1, uuid.toString());
				 stmt.setString(2, "success");
				 stmt.setTimestamp(3, new Timestamp(now));
				 stmt.setInt(4,userId);
				 stmt.setString(5,emailId);
				 
				 int i = stmt.executeUpdate();
				 if(i==1) {
					 
					 SetCookie sc = SetCookie.create("sessionTrack", uuid.toString());
					 jb = JSON.createObjectBuilder()
							 .add("InsertStatus", "Success")
							 .add("sessionTrack", uuid.toString())
							 .add("user_id", userId)
							 .build();
				 } else {
					 
					 jb = JSON.createObjectBuilder().add("InsertStatus", "Hanged").build();
				 }
				 
			 } else {
				 
				 LOGGER.info("Where resultset is failed");
				 jb = JSON.createObjectBuilder().add("InsertStatus", "Fail").build();
				 
				 
			 }
			 
			 
		 }catch(Exception e) {
			 e.printStackTrace();
		 } finally{
			 connection.close();
		 }
		 return jb;
		}
	 
	 	@Path("/validateCookie")
	    @GET
	    @Produces(MediaType.TEXT_HTML)
	 	@CrossOrigin(value = {"http://localhost:8129/","http://localhost:8180/"},
	       allowMethods = {HttpMethod.POST,HttpMethod.GET})
	    public  Response validateCookie(@QueryParam("trackId") String trackId,@QueryParam("userId") String userId) throws URISyntaxException, SQLException {
	    	
	 		LOGGER.info("DBOperations:checkRouteRegister"+"   Form Param : trackId===>"+trackId+"<=======userId===>"+userId);
	 		URI ui = null;
	 		 String checkSql = "select LOGIN_TIME, LOGIN_STATUS from USER_SSO where SESSION_ID = ? and USER_ID = ?";
	 		Connection connection = null;
	 		 try {
				 
				System.out.println(userId);
				connection = this.dataSource.getConnection();
				PreparedStatement st = connection.prepareStatement(checkSql);
				int t = Integer.parseInt(userId);
				st.setString(1, trackId);
				st.setInt(2, t);
					 
				ResultSet rs = st.executeQuery();
				
				Timestamp ts = null;
				String loginStatus = null;
				while(rs.next()) {
					ts = rs.getTimestamp("LOGIN_TIME");
					loginStatus = rs.getString("LOGIN_STATUS");
				}
				LOGGER.info("Time Stamp       =======>  "+ts+"   and Login status ===========>"+loginStatus);
				if(calculateTimeDiff(ts) > 20) {
					 ui = new URI("http://localhost:8179/timeout.html");
				} else {
					 ui = new URI("http://localhost:8179/success.html");
				}
			 }catch(Exception e) {
				 e.printStackTrace();
			 }finally{
				 connection.close();
			 }
	 		System.out.println("UI Get ----->"+ui);
			 return Response.seeOther(ui)
					 .header("Access-Control-Allow-Origin","*")
					 .build();
	    }
	 	
	 	public long calculateTimeDiff(Timestamp ts) {
	 		
	 		SimpleDateFormat fm = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
	 	     long now = System.currentTimeMillis();
	 	     
	 	     Timestamp ps = new Timestamp(now);
	 	      
	 	     Date loginTime = new Date(ts.getTime());
	 	     Date currentTime = new Date(ps.getTime());
	 	     
	 	    try {
	 	    	loginTime = fm.parse(loginTime.toString());
	 	       currentTime = fm.parse(currentTime.toString());
	 	    } catch (Exception e) {
	 	        e.printStackTrace();
	 	    }    
	 	    
	 	   LOGGER.info("loginTime : "+loginTime);
	 	  LOGGER.info("currentTime : "+currentTime);
	 	      
	 	      long diff = currentTime.getTime() - loginTime.getTime();
	 	      long diffMinutes = diff / (60 * 1000);
	 	     LOGGER.info("Time in minutes: " + diffMinutes + " minutes.");   

	 		return diffMinutes;
	 	}

		
		
	
	

}
