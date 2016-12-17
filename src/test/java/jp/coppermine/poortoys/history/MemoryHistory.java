package jp.coppermine.poortoys.history;

public class MemoryHistory extends AbstractHistory {
	
	public MemoryHistory() {
		super();
	}
	
	public MemoryHistory(int limit) {
		super(limit);
	}
	
	@Override
	public void load() {
		// do nothing
	}

	@Override
	public void save() {
		// do nothing
	}

}
