import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Student {
	private final String stuID;
	private Map<Integer, String> borrowingBooks= new HashMap<>();
	
	Student(String stuID){
		this.stuID = stuID;
	}

	public String getStuID() {
		return stuID;
	}
	
	/**
	 * 尝试将一本书添加到学生的已借图书中
	 * @param id 要借的图书ID
	 * @return 成功/失败
	 */
	boolean addToBorrowingBooks(int id) {
		if(borrowingBooks.size() >= 3) {
			System.out.println("This student has borrowed 3 books, and reached limit");
			return false;
		}
		if(borrowingBooks.get(id) != null) {
			System.out.println("This student has borrowed the same book.");
			return false;
		}
		String borrowTime = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();//获得借书的时间戳
		borrowTime = borrowTime.replace("T", " ");//格式化便于阅读
		borrowTime = borrowTime.replace("Z", " ");
		borrowingBooks.put(id, borrowTime);//添加
		return true;
	}
	
	/**
	 * 将一条借书记录保存到清单当中
	 * @param id 图书ID
	 * @param borrowTime 借出的时间
	 * @return 成功/失败
	 */
	boolean addToBorrowingBooks(int id, String borrowTime) {
		if(borrowingBooks.size() >= 3) {
			System.out.println("This student has borrowed 3 books, and reached limit");
			return false;
		}
		if(borrowingBooks.get(id) != null) {
			System.out.println("This student has borrowed the same book.");
			return false;
		}
		
		borrowingBooks.put(id, borrowTime);
		return true;
	}
	
	/**
	 * 尝试将一本书移出借书清单(即还书的第一步)
	 * @param id 鱼还图书
	 * @return 成功/失败
	 */
	boolean removeFromBorrowingBooks(int id) {
		if(borrowingBooks.containsKey(id) == false) {
			System.out.println("This Student Didn't Borrow Book with this BookID: " + id);
			return false;
		}
		borrowingBooks.remove(id);
		System.out.println("Book Returning to the Library");
		return true;
	}
	
	int getBorrowingBooksNum() {
		return borrowingBooks.size();
	}
	
	Set<Integer> allBorrowingBooksID() {
		 return borrowingBooks.keySet();
	}
	
	String getBorrowTime(Integer bookID) {
		return borrowingBooks.get(bookID);
	}
}
