package jp.coppermine.poortoys.history;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;

import org.junit.Test;

import thirdparty.AnotherHistory;

public class HistoryTest {

	@Test
	public void testOf_FileHistory() {
		System.setProperty("poortoys.history.file.path", Paths.get(System.getProperty("user.home"), ".ifocatcher").toString());
		System.setProperty("poortoys.history.file.charset", "UTF-8");
		System.setProperty("poortoys.history.size", "10");
		
		History history = History.of(FileHistory.class);
		
		assertThat(history, is(instanceOf(History.class)));
		assertThat(history, is(instanceOf(FileHistory.class)));
	}

	@Test
	public void testOf_MemoryHistory() {
		System.setProperty("poortoys.history.file.path", Paths.get(System.getProperty("user.home"), ".ifocatcher").toString());
		System.setProperty("poortoys.history.file.charset", "UTF-8");
		System.setProperty("poortoys.history.size", "10");
		
		History history = History.of(MemoryHistory.class);
		
		assertThat(history, is(instanceOf(History.class)));
		assertThat(history, is(instanceOf(MemoryHistory.class)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOf_UnknownHistory() {
		System.setProperty("poortoys.history.file.path", Paths.get(System.getProperty("user.home"), ".ifocatcher").toString());
		System.setProperty("poortoys.history.file.charset", "UTF-8");
		System.setProperty("poortoys.history.size", "10");
		
		History.of(UnknownHistory.class);
	}

	@Test
	public void testOf_ThirdParty_AnotherHistory() {
		System.setProperty("poortoys.history.file.path", Paths.get(System.getProperty("user.home"), ".ifocatcher").toString());
		System.setProperty("poortoys.history.file.charset", "UTF-8");
		System.setProperty("poortoys.history.size", "10");
		
		History history = History.of(AnotherHistory.class);
		
		assertThat(history, is(instanceOf(History.class)));
		assertThat(history, is(instanceOf(AnotherHistory.class)));
	}

	@Test
	public void testGetDefault() {
		System.setProperty("poortoys.history.file.path", Paths.get(System.getProperty("user.home"), ".ifocatcher").toString());
		System.setProperty("poortoys.history.file.charset", "UTF-8");
		System.setProperty("poortoys.history.size", "10");
		
		History history = History.getDefault();
		
		assertThat(history, is(instanceOf(History.class)));
		assertThat(history, is(instanceOf(FileHistory.class)));
	}

}
