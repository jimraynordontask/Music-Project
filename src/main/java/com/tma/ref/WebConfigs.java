package com.tma.ref;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.tma.db.song.*;
import com.tma.db.playlist.*;
import com.tma.db.user.MongoUserDatabaseServiceImpl;
import com.tma.db.user.UserDatabaseService;
import com.tma.mainprocessing.*;
import com.tma.security.WebUserDetailsService;

@Configuration
@ComponentScan(basePackages = "com.technicalkeeda")
@EnableMongoRepositories({ "com.tma" })
public class WebConfigs {

	 @Bean
	public MongoClient webMongoClient() {
		try{
			return new MongoClient(new ServerAddress("192.168.70.204", 27017));
		}
		catch (Exception e) {
			System.out.println("Could not connect to MongoDB Server!");
			System.exit(1);
			return null;
		}
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		try{
			return new MongoTemplate(webMongoClient(), "WebDie");
		}catch(Exception e){
			System.out.println("Could not connect to database!");
			System.exit(2);
			return null;
		}
	}
	
	@Bean
	public SongDatabaseService songDatabaseService() {
		return (SongDatabaseService) new  MongoSongServiceImpl(); 
	}
	@Bean
	public PlaylistDatabaseService playlistDatabaseService() {
		return (PlaylistDatabaseService) new  MongoPlaylistServiceImpl(); 
	}
	
	@Bean
	public UserDatabaseService userDatabaseService() {
		return (UserDatabaseService) new  MongoUserDatabaseServiceImpl(); 
	}
	
	@Bean
	public SongMainProcessing songMainProcessing() {
		return new SongMainProcessing();
	}
	@Bean
	public PlaylistMainProcessing playlistMainProcessing() {
		return new PlaylistMainProcessing();
	}
	
	@Bean(name = "webUserDetailsService")
	public UserDetailsService webUserDetailsService() {
		return new WebUserDetailsService();
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return  new CommonsMultipartResolver();
	}
}
