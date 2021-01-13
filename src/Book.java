import java.util.*;

class Book {
	private final String name;
	private final ArrayList<String> author = new ArrayList<String>();
	private final String press;
	private int totalNum;
	private int id;
	
	Book(String name, List<String> author, String press, int totalNum) {
		this.name = name;
		for(String s : author) {
			this.author.add(s);
		}
		this.press = press;
		this.totalNum = totalNum;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((press == null) ? 0 : press.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object o) {//在名称与出版社相同时认为是同一种书
		if (o instanceof Book) {
			Book b = (Book) o;
			return  Objects.equals(this.name, b.name)
					&& Objects.equals(this.press, b.press)
					&& compareAuthor(this.author, b.author);
		}
		return false;
	}
	
	/**
	 * 比较作者是否相同
	 * @param a
	 * @param b
	 * @return
	 */
	static boolean compareAuthor(ArrayList<String> a, ArrayList<String> b) {
		if(a.size() != b.size())
			return false;
		boolean result = false;
		for(String aa : a) {
			for(String bb : b) {
				if(aa.equals(bb)) {
					result = true;
					break;
				}	
			}
			if(result)
				break;
			result = false;
		}
		return result;
	}
	
	String getName() {
		return this.name;
	}

	ArrayList<String> getAuthor() {
		return this.author;
	}

	String getPress() {
		return this.press;
	}

	int getTotalNum() {
		return this.totalNum;
	}
	
	void setTotalNum(int num) {
		this.totalNum = num;
	}

	int getID() {
		return this.id;
	}
	
	void setID(int id) {
		this.id = id;
	}
	
	void println() {
		System.out.print(this.name + "\t");
		System.out.print(this.press + "\t");
		System.out.println(this.author);
	}
}
