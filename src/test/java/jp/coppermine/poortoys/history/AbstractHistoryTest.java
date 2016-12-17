package jp.coppermine.poortoys.history;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ListIterator;

import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractHistoryTest {
    
    @Before
    public void setUp() {
        System.clearProperty("poortoys.history.size");
    }
    
	@Test
	public void testAbstractHistory() {
		AbstractHistory hist = new MemoryHistory();
		
		assertThat(hist, is(instanceOf(AbstractHistory.class)));
		assertThat(hist.getMaxSize(), is(128));
	}

	@Test
	public void testAbstractHistory_Int() {
		AbstractHistory hist = new MemoryHistory(200);
		
		assertThat(hist, is(instanceOf(AbstractHistory.class)));
		assertThat(hist.getMaxSize(), is(200));
	}

	@Test
	public void testAbstractHistory_Zero() {
		AbstractHistory hist = new MemoryHistory(0);
		
		assertThat(hist, is(instanceOf(AbstractHistory.class)));
		assertThat(hist.getMaxSize(), is(0));
		
		hist.append(Command.of("command"));
		
		assertThat(hist.list().size(), is(0));
	}

	@Test
	public void testGetCommands_init() {
		AbstractHistory hist = new MemoryHistory();
		
		assertThat(hist.getCommands(), is(not(nullValue())));
		assertThat(hist.getCommands().size(), is(0));
	}

	@Test
	public void testGetCommands_add_1() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("first"));
		
		assertThat(hist.getCommands(), is(not(nullValue())));
		assertThat(hist.getCommands().size(), is(1));
	}

	@Test
	public void testGetCommands_add_2() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("first"));
		hist.append(Command.of("second"));
		
		assertThat(hist.getCommands(), is(not(nullValue())));
		assertThat(hist.getCommands().size(), is(2));
	}

	@Test
	public void testGetCommands_add_3_hasDuplicate() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("first"));
		hist.append(Command.of("second"));
		hist.append(Command.of("first"));
		
		assertThat(hist.getCommands(), is(not(nullValue())));
		assertThat(hist.getCommands().size(), is(3));
	}

	@Test
	public void testGetCommands_cursor() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("first"));
		hist.append(Command.of("second"));
		hist.append(Command.of("first"));
		hist.append(Command.of("third"));
		hist.append(Command.of("recent"));
		
		ListIterator<Command> listIterator = hist.getCommands().listIterator();
		
		assertThat(listIterator.hasPrevious(), is(false));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("recent"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("third"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("first"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("second"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("first"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(false));
		assertThat(listIterator.previous().getCommand(), is("first"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.previous().getCommand(), is("second"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.previous().getCommand(), is("first"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.previous().getCommand(), is("third"));
		
		assertThat(listIterator.hasPrevious(), is(true));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.previous().getCommand(), is("recent"));
		
		assertThat(listIterator.hasPrevious(), is(false));
		assertThat(listIterator.hasNext(), is(true));
		assertThat(listIterator.next().getCommand(), is("recent"));
		
	}

	@Test
	public void testGetMaxSize_default() {
		AbstractHistory hist = new MemoryHistory();
		
		assertThat(hist.getMaxSize(), is(128));
	}

	@Test
	public void testGetMaxSize_specified() {
		AbstractHistory hist = new MemoryHistory(50);
		
		assertThat(hist.getMaxSize(), is(50));
	}

	@Test
	public void testList() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("first"));
		hist.append(Command.of("second"));
		hist.append(Command.of("first"));
		hist.append(Command.of("third"));
		hist.append(Command.of("recent"));
		
		assertThat(hist.list().size(), is(5));
		assertThat(hist.list().get(4).getCommand(), is("first"));
		assertThat(hist.list().get(3).getCommand(), is("second"));
		assertThat(hist.list().get(2).getCommand(), is("first"));
		assertThat(hist.list().get(1).getCommand(), is("third"));
		assertThat(hist.list().get(0).getCommand(), is("recent"));
	}

	@Test
	public void testAdd() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("some command"));
		
		assertThat(hist.list().get(0).getCommand(), is("some command"));
	}

	@Test
	public void testAdd_overflow1() {
		AbstractHistory hist = new MemoryHistory(5);
		hist.append(Command.of("command #1"));
		hist.append(Command.of("command #2"));
		hist.append(Command.of("command #3"));
		hist.append(Command.of("command #4"));
		hist.append(Command.of("command #5"));
		
		assertThat(hist.list().size(), is(5));
		assertThat(hist.list().get(0).getCommand(), is("command #5"));
		assertThat(hist.list().get(4).getCommand(), is("command #1"));
	}

	@Test
	public void testAdd_overflow2() {
		AbstractHistory hist = new MemoryHistory(5);
		hist.append(Command.of("command #1"));
		hist.append(Command.of("command #2"));
		hist.append(Command.of("command #3"));
		hist.append(Command.of("command #4"));
		hist.append(Command.of("command #5"));
		hist.append(Command.of("command #6"));
		
		assertThat(hist.list().size(), is(5));
		assertThat(hist.list().get(0).getCommand(), is("command #6"));
		assertThat(hist.list().get(4).getCommand(), is("command #2"));
	}

	@Test
	public void testClear() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("command #1"));
		hist.append(Command.of("command #2"));
		hist.append(Command.of("command #3"));
		hist.append(Command.of("command #4"));
		hist.append(Command.of("command #5"));
		
		assertThat(hist.list().size(), is(5));
		
		hist.clear();
		
		assertThat(hist.list().size(), is(0));
	}

	@Test
	public void testShrink() {
		AbstractHistory hist = new MemoryHistory();
		hist.append(Command.of("command #1", LocalDate.of(2015, 10, 29).atStartOfDay()));
		hist.append(Command.of("command #2", LocalDate.of(2015, 10, 30).atStartOfDay()));
		hist.append(Command.of("command #3", LocalDate.of(2015, 10, 31).atStartOfDay()));
		hist.append(Command.of("command #4", LocalDate.of(2015, 11, 1).atStartOfDay()));
		hist.append(Command.of("command #5", LocalDate.of(2015, 11, 2).atStartOfDay()));
		
		LocalDateTime expired = LocalDate.of(2015, 11, 2).minusDays(1).atStartOfDay();
		hist.shrink(expired);
		
		assertThat(hist.list().size(), is(1));
		assertThat(hist.list().get(0).getCommand(), is("command #5"));
	}
}
