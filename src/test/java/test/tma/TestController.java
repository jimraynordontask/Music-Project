package test.tma;

import java.net.URI;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tma.controller.SongController;
import com.tma.db.song.MongoSongServiceImpl;
import com.tma.db.song.SongDatabaseService;
import com.tma.model.Song;
import com.tma.model.SongWithUrl;
import com.tma.ref.Libraries;
import com.tma.ref.RequestType;
import com.tma.ref.ResponseStatus;
import com.tma.ref.WebConfigs;
import com.tma.ref.WebResponse;
import junit.framework.TestCase;

import java.util.ArrayList;

import javax.servlet.Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration("file:src/main/webapp/WEB-INF/WebCom-servlet.xml"),
		@ContextConfiguration("file:src/main/webapp/WEB-INF/security.xml"),
		@ContextConfiguration(classes = WebConfigs.class) })
public class TestController extends TestCase {

	// ApplicationContext appContext = new
	// AnnotationConfigApplicationContext(WebConfigs.class);
	@org.mockito.Mock
	SongDatabaseService databaseService = new MongoSongServiceImpl();

	MockMvc mockMvc;

	@Autowired
	Filter springSecurityFilterChain;

	@Autowired
	UserDetailsService webUserDetailsService;

	@InjectMocks
	SongController webController = new SongController();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		webController.setDatabaseService(databaseService);
		mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
	}
	
	public String getAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", username);
		params.add("password", password);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Basic dHJ1c3RlZCBjbGllbnQ6c2VjcmV0");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/oauth/token")
				.headers(httpHeaders).params(params).accept("application/json;charset=UTF-8");
		ResultActions result = mockMvc.perform(mockHttpServletRequestBuilder);

		int resultStatus = result.andReturn().getResponse().getStatus();
		String resultString = result.andReturn().getResponse().getContentAsString();
		return resultStatus + resultString;

		//JacksonJsonParser jsonParser = new JacksonJsonParser();
		//return jsonParser.parseMap(resultString).get("access_token").toString();
	}
	
	@Test
	public void Test1() throws Exception {
		System.out.println("abcd:"+getAccessToken("admin", "admin"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testListEmptyWithRoleAdmin() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				new ArrayList<SongWithUrl>());
		String expectedResponseInString = Libraries.responseToJson(expectedResponseObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseInString));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testListWithRoleAdmin() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		ArrayList<SongWithUrl> mockListSongInObjectsResult = new ArrayList<SongWithUrl>();
		Song exampleSong1 = new Song("Alone", "Pop");
		exampleSong1.setId("59f9afe4c99edf05490886b4");
		Song exampleSong2 = new Song("Dance", "Rock");
		exampleSong2.setId("16f3dfe4c99edf05490886b0");
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong1));
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong2));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				mockListSongInObjectsResult);
		String expectedResponseStr = Libraries.responseToJson(expectedResponseObject);
		Mockito.when(databaseService.getAllSongs()).thenReturn(mockListSongInObjectsResult);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test Get song success.
	public void testGetSongWithRoleAdmin() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/tma"));
		Song mockSongResultInObject = new Song("tma", "com");
		mockSongResultInObject.setId("59f9afe4c99edf05490886b0");
		String expectedResponseStr = Libraries.responseToJson(
				new WebResponse(RequestType.GET, ResponseStatus.OK, null, new SongWithUrl(mockSongResultInObject)));
		Mockito.when(databaseService.getSong("tma")).thenReturn(mockSongResultInObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test Get song failed.
	public void testFailedGetSong() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/Song+not+exists"));
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.GET, ResponseStatus.FAILED, "No song found!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the postt add song failed.
	public void testFailedAddSong() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs"));
		// String expectedResponse = Libraries
		// .responseToJson(new WebResponse(RequestType.ADD, ResponseStatus.FAILED,
		// "Malformed Request!", null));;
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the add song.
	public void testAddSongWithRoleAdmin() throws Exception {

		//String access_token = getAccessToken("admin", "admin");
		
		MockMultipartFile file = new MockMultipartFile("file", "dummy.mp3", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.ADD, ResponseStatus.OK, null, null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testAddSongInvalidFileType() throws Exception {

		//String accessToken = getAccessToken("admin", "nimda");
		MockMultipartFile file = new MockMultipartFile("file", "dummy.mp5", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Invalid File Type!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testAddSongNoFileExtension() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "dummy", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.ADD, ResponseStatus.FAILED, "Invalid File Type!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song failed.
	public void testFailedEditSong() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.put(new URI("/api/songs"));
		// String expectedResponse = Libraries
		// .responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.FAILED,
		// "Malformed Request!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song which is not found.
	public void testEditSongNotFound() throws Exception {
		Song userEditSongRequest = new Song("támda", "Pop");
		String json = Libraries.songToJson(userEditSongRequest);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/asdasd")).contentType(MediaType.APPLICATION_JSON).content(json);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Song not found!", null));
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song.
	public void testEditSongWithRoleAdmin() throws Exception {
		Song userEditSongRequest = new Song("tman", "comn");
		String json = new ObjectMapper().writeValueAsString(userEditSongRequest);
		// System.out.println(json);
		// String json = new Jcak;
		// System.out.println("Request Body In: " + json);
		Mockito.when(databaseService.updateSong("tma", "tman", "comn")).thenReturn(true);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/tma")).contentType(MediaType.APPLICATION_JSON).content(json);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.OK, null, null));
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test delete
	public void testFailedDelete() throws Exception {
		// MultiValueMap<String, String> params = new LinkedMultiValueMap<String,
		// String>();
		// params.add("name", "tma");

		String json = "{\"name\":\"sdfsd\"}";

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs")).contentType(MediaType.APPLICATION_JSON).content(json);
		// String expectedResponse = Libraries
		// .responseToJson(new WebResponse(RequestType.DELETE, ResponseStatus.FAILED,
		// "Song not found!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test delete
	public void testDeleteWithRoleAdmin() throws Exception {
		// MultiValueMap<String, String> parametersOfHttpDeleteRequest = new
		// LinkedMultiValueMap<String, String>();
		// parametersOfHttpDeleteRequest.add("name", "tmai");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs/tmai"));
		Mockito.when(databaseService.deleteSong("tmai")).thenReturn(true);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "File not deleted!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
				.andExpect(content().string(expectedResponse));
	}

	// ======================================================================================

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testListEmptyT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		WebResponse response = new WebResponse(RequestType.LIST, ResponseStatus.OK, null, new ArrayList<SongWithUrl>());
		String expectedResponse = Libraries.responseToJson(response);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	public void testListT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		ArrayList<SongWithUrl> listSong = new ArrayList<SongWithUrl>();
		Song song1 = new Song("Alone", "Pop");
		song1.setId("59f9afe4c99edf05490886b4");
		Song song2 = new Song("Dance", "Rock");
		song2.setId("16f3dfe4c99edf05490886b0");
		listSong.add(new SongWithUrl(song1));
		listSong.add(new SongWithUrl(song2));
		WebResponse response = new WebResponse(RequestType.LIST, ResponseStatus.OK, null, listSong);
		String expectedResponse = Libraries.responseToJson(response);
		Mockito.when(databaseService.getAllSongs()).thenReturn(listSong);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test Get song success.
	public void testGetSongT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/tma"));
		Song song = new Song("tma", "com");
		song.setId("59f9afe4c99edf05490886b0");
		Mockito.when(databaseService.getSong("tma")).thenReturn(song);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test Get song failed.
	public void testFailedGetSongT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/Song+not+exists"));
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.GET, ResponseStatus.FAILED, "No song found!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the post add song failed.
	public void testFailedAddSongT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs"));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the add song.
	public void testAddSongT() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "dummy.mp3", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk());
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song failed.
	public void testFailedEditSongT() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.put(new URI("/api/songs"));
		// String expectedResponse = Libraries
		// .responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.FAILED,
		// "Malformed Request!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song.
	public void testEditSongNotFoundT() throws Exception {
		Song userEditSongRequest = new Song("támda", "Pop");
		String json = new ObjectMapper().writeValueAsString(userEditSongRequest);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/asfasdfsdf")).contentType(MediaType.APPLICATION_JSON).content(json);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.FAILED, "Song not found!", null));
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test the put edit song.
	public void testEditSongT() throws Exception {
		Song userEditSongRequest = new Song("támda", "Pop");
		String json = new ObjectMapper().writeValueAsString(userEditSongRequest);
		Mockito.when(databaseService.updateSong("tma", "támda", "Pop")).thenReturn(true);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/1")).contentType(MediaType.APPLICATION_JSON).content(json);
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk());
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test delete
	public void testFailedDeleteT() throws Exception {
		// MultiValueMap<String, String> params = new LinkedMultiValueMap<String,
		// String>();
		// params.add("name", "tma");
		String json = "{\"name\":\"sdfsd\"}";

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs")).contentType(MediaType.APPLICATION_JSON).content(json);
		// String expectedResponse = Libraries
		// .responseToJson(new WebResponse(RequestType.DELETE, ResponseStatus.FAILED,
		// "Song not found!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().is4xxClientError());
	}

	@Test(timeout = 1000)
	@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
	// Test delete
	public void testDeleteT() throws Exception {
		// MultiValueMap<String, String> parametersOfHttpDeleteRequest = new
		// LinkedMultiValueMap<String, String>();
		// parametersOfHttpDeleteRequest.add("name", "tmai");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs/tmai"));
		Mockito.when(databaseService.deleteSong("tmai")).thenReturn(true);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk());
	}

	// =================================================================================================================================
	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	// Test Get song success.
	public void testGetSongWithRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/tma"));
		Song mockSongResultInObject = new Song("tma", "com");
		mockSongResultInObject.setId("59f9afe4c99edf05490886b0");
		String expectedResponseStr = Libraries.responseToJson(
				new WebResponse(RequestType.GET, ResponseStatus.OK, null, new SongWithUrl(mockSongResultInObject)));
		Mockito.when(databaseService.getSong("tma")).thenReturn(mockSongResultInObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	public void testListEmptyWithRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				new ArrayList<SongWithUrl>());
		String expectedResponseInString = Libraries.responseToJson(expectedResponseObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseInString));
	}

	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	public void testListWithRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		ArrayList<SongWithUrl> mockListSongInObjectsResult = new ArrayList<SongWithUrl>();
		Song exampleSong1 = new Song("Alone", "Pop");
		exampleSong1.setId("59f9afe4c99edf05490886b4");
		Song exampleSong2 = new Song("Dance", "Rock");
		exampleSong2.setId("16f3dfe4c99edf05490886b0");
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong1));
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong2));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				mockListSongInObjectsResult);
		String expectedResponseStr = Libraries.responseToJson(expectedResponseObject);
		Mockito.when(databaseService.getAllSongs()).thenReturn(mockListSongInObjectsResult);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	// Test the add song.
	public void testAddSongWithRoleUser() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "dummy.mp3", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	// Test the put edit song.
	public void testEditSongWithRoleUser() throws Exception {
		Song userEditSongRequest = new Song("tman", "comn");
		String json = new ObjectMapper().writeValueAsString(userEditSongRequest);
		// System.out.println(json);
		// String json = new Jcak;
		// System.out.println("Request Body In: " + json);
		Mockito.when(databaseService.updateSong("tma", "tman", "comn")).thenReturn(true);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/tma")).contentType(MediaType.APPLICATION_JSON).content(json);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.OK, null, null));
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test
	@WithMockUser(username = "user", authorities = { "ROLE_USER" })
	// Test delete
	public void testDeleteWithRoleUser() throws Exception {
		// MultiValueMap<String, String> parametersOfHttpDeleteRequest = new
		// LinkedMultiValueMap<String, String>();
		// parametersOfHttpDeleteRequest.add("name", "tmai");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs/tmai"));
		Mockito.when(databaseService.deleteSong("tmai")).thenReturn(true);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "File not deleted!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
				.andExpect(content().string(expectedResponse));
	}

	// ===========================================================================================================

	@Test
	// Test Get song success.
	public void testGetSongUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/tma"));
		Song mockSongResultInObject = new Song("tma", "com");
		mockSongResultInObject.setId("59f9afe4c99edf05490886b0");
		String expectedResponseStr = Libraries.responseToJson(
				new WebResponse(RequestType.GET, ResponseStatus.OK, null, new SongWithUrl(mockSongResultInObject)));
		Mockito.when(databaseService.getSong("tma")).thenReturn(mockSongResultInObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	public void testListEmptyUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				new ArrayList<SongWithUrl>());
		String expectedResponseInString = Libraries.responseToJson(expectedResponseObject);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseInString));
	}

	@Test
	public void testListUnauthorized() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get(new URI("/api/songs/"));
		ArrayList<SongWithUrl> mockListSongInObjectsResult = new ArrayList<SongWithUrl>();
		Song exampleSong1 = new Song("Alone", "Pop");
		exampleSong1.setId("59f9afe4c99edf05490886b4");
		Song exampleSong2 = new Song("Dance", "Rock");
		exampleSong2.setId("16f3dfe4c99edf05490886b0");
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong1));
		mockListSongInObjectsResult.add(new SongWithUrl(exampleSong2));
		WebResponse expectedResponseObject = new WebResponse(RequestType.LIST, ResponseStatus.OK, null,
				mockListSongInObjectsResult);
		String expectedResponseStr = Libraries.responseToJson(expectedResponseObject);
		Mockito.when(databaseService.getAllSongs()).thenReturn(mockListSongInObjectsResult);
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponseStr));
	}

	@Test
	// Test the add song.
	public void testAddSongUnauthorized() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "dummy.mp3", null, "Some dataset...".getBytes());

		Mockito.when(databaseService.addSong(Mockito.any(Song.class))).thenReturn("5017134bgbhj15g1");
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart(new URI("/api/songs")).file(file).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content("{\"name\":\"ccc\",\"gerne\":\"Not\"}");
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk());
	}

	@Test
	// Test the put edit song.
	public void testEditSongUnauthorized() throws Exception {
		Song userEditSongRequest = new Song("tman", "comn");
		String json = new ObjectMapper().writeValueAsString(userEditSongRequest);
		// System.out.println(json);
		// String json = new Jcak;
		// System.out.println("Request Body In: " + json);
		Mockito.when(databaseService.updateSong("tma", "tman", "comn")).thenReturn(true);

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.put(new URI("/api/songs/tma")).contentType(MediaType.APPLICATION_JSON).content(json);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.EDIT, ResponseStatus.OK, null, null));
		ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
		resultActions.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(content().string(expectedResponse));
	}

	@Test
	// Test delete
	public void testDeleteUnauthorized() throws Exception {
		// MultiValueMap<String, String> parametersOfHttpDeleteRequest = new
		// LinkedMultiValueMap<String, String>();
		// parametersOfHttpDeleteRequest.add("name", "tmai");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.delete(new URI("/api/songs/tmai"));
		Mockito.when(databaseService.deleteSong("tmai")).thenReturn(true);
		String expectedResponse = Libraries
				.responseToJson(new WebResponse(RequestType.DELETE, ResponseStatus.FAILED, "File not deleted!", null));
		mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(status().isOk())
				.andExpect(content().string(expectedResponse));
	}

}
