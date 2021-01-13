import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Librarian {
	private static int typeOfBooks = 1;
	private Map<Book, Integer> books = new LinkedHashMap<>();
	private ArrayList<Student> stus = new ArrayList<>();

	/**
	 * 添加图书或者改变图书书目
	 * 
	 * @param b   待改变/添加的图书
	 * @param num 目标数目,为0代表添加
	 * @return 改变成功/失败(图书数小于零)
	 */
	boolean addBooks(Book b, int num) {
		if (books.containsKey(b)) {
			if (num < 0 && ((b.getTotalNum() < Math.abs(num)) || (books.get(b) < Math.abs(num)))) {// 总数或剩余数小于零, 非法
				System.out.print("Invalid input: num of this book cannot below 0, but input would change the num to "
						+ (b.getTotalNum() + num));
				System.out.println("(total num) and " + (books.get(b) + num) + " (remain num)");
				return false;
			}
			System.out.println("This existing book's total num has been changed");
			Book change = null;//遍历查找要改变的那本书
			for (Book iter : books.keySet()) {
				if (iter.equals(b)) {
					change = iter;
					break;
				}
			}
			try {
				change.setTotalNum(change.getTotalNum() + num);
				Integer remainNum = books.get(change);
				remainNum += num;
				books.put(change, remainNum);
			} catch (NullPointerException e) {
				System.out.println("NPE Exception~");
				e.printStackTrace();
			}
			return true;
		} else {
			System.out.println("This book has been added.");
			b.setID(typeOfBooks);
			@SuppressWarnings("deprecation")
			Integer newInt = new Integer(b.getTotalNum());//避免不同书的余量相同时指向同一个Integer实例,导致同时修改两个
			books.put(b, newInt);
			typeOfBooks++;
		}

		System.out.println("Book successfully changed.");
		return true;
	}

	/**
	 * 尝试删除一本书
	 * @param id 要删除的书的ID
	 * @return 成功/失败
	 */
	boolean deleteBooks(int id) {
		Book b = searchID(id);
		if (b == null) {//图书不存在
			System.out.println("Book with Given ID Not Found: ID " + id);
			return false;
		}
		if (books.get(b) != b.getTotalNum()) {//存在借出
			System.out.println("At Least One of This Book Has Been Borrowed Out:" + (b.getTotalNum() - books.get(b)));
			return false;
		}
		try {
			books.remove(b);
		} catch (Exception e) {
			System.out.println("Delete Process Failed");
			return false;
		}
		System.out.println("Succseefully Deleted Books with ID " + id);
		return true;
	}

	/**
	 * 尝试借出一本书(借书第一步)
	 * @param id 要借出的书的ID
	 * @param stuID 借书者ID
	 * @return 成功/失败
	 */
	boolean lentOutBook(int id, String stuID) {
		Book b = searchID(id);
		if (b == null) {
			System.out.println("Book with Given ID Not Found: ID " + id);
			return false;
		}
		if (getRemainNum(id) <= 0) {
			System.out.println("All books of this kind have been borrrowed...");
			return false;
		}
		Student currStu = searchStudent(stuID);
		if (currStu == null) {
			System.out.println("Student with Given ID Not Found: StuID " + stuID);
			return false;
		}
		if (currStu.addToBorrowingBooks(id) == false) {//没能添加到借书者的已借图书清单, 借出失败
			return false;
		}
		Integer newRemainNum = books.get(b) - 1;//余量-1
		books.put(b, newRemainNum);//更新余量
		return true;
	}

	/**
	 * 尝试将一本借出的书放回去(还书第二步)
	 * @param id 要还的书的ID
	 * @param stuID 借书者ID
	 * @return 成功/失败
	 */
	boolean getReturnBook(int id, String stuID) {
		Book b = searchID(id);
		if (b == null) {
			System.out.println("Book with Given ID Not Found: ID " + id);
			return false;
		}
		Student currStu = searchStudent(stuID);
		if (currStu == null) {
			System.out.println("Student with Given ID Not Found: StuID " + stuID);
			return false;
		}
		if (currStu.removeFromBorrowingBooks(id) == false) {//没能从借书者的已借图书清单移除, 还书失败
			System.out.println("Book Failed to Return");
			return false;
		}
		Integer newRemainNum = books.get(b) + 1;//余量+1
		books.put(b, newRemainNum);//更新
		System.out.println("Book Successfully Returned to Library");
		return true;
	}

	/**
	 * 按照书名模糊查找, 可能返回多本
	 * @param bookName 书名
	 * @return ArrayList<Book>, 符合书名的所有图书
	 */
	ArrayList<Book> searchName(String bookName) {

		if (bookName.equals(""))
			return null;

		ArrayList<Book> result = new ArrayList<>();
		bookName = bookName.toLowerCase();
		for (Book b : books.keySet()) {
			if (b.getName().toLowerCase().contains(bookName)) {//统一小写, contains()判断包含
				result.add(b);
			}
		}
		return result;
	}

	/**
	 * 按照作者查找, 可能返回多本
	 * @param authorName 作者名
	 * @return ArrayList<Book>, 符合的所有图书
	 */
	ArrayList<Book> searchAuthor(String authorName) {

		if (authorName.equals(""))
			return null;
		ArrayList<Book> result = new ArrayList<>();
		Iterator<Map.Entry<Book, Integer>> iter = books.entrySet().iterator();

		authorName = authorName.toLowerCase();
		while (iter.hasNext()) {
			Map.Entry<Book, Integer> b = iter.next();
			for (String s : b.getKey().getAuthor()) {
				if (s.toLowerCase().equals(authorName))
					result.add(b.getKey());
			}
		}
		return result;
	}

	/**
	 * 按照出版社查找, 可能返回多本
	 * @param pressName 出版社名
	 * @return ArrayList<Book>, 符合的所有图书
	 */
	ArrayList<Book> searchPress(String pressName) {

		if (pressName.equals("")) {
			return null;
		}

		ArrayList<Book> result = new ArrayList<>();
		pressName = pressName.toLowerCase();
		for (Book b : books.keySet()) {
			if (b.getPress().toLowerCase().equals(pressName)) {
				result.add(b);
			}
		}
		return result;
	}

	Book searchID(int id) {
		for (Book b : books.keySet()) {
			if (b.getID() == id)
				return b;
		}
		return null;
	}

	Book isExist(Book in) {
		for (Book b : books.keySet()) {
			if (in.equals(b))
				return b;
		}
		return null;
	}

	int getTypeOfBooks() {
		return typeOfBooks;
	}

	Integer getRemainNum(int id) {
		Book b = searchID(id);
		if (b == null)
			return -1;
		return books.get(b);
	}

	boolean registerStudent(String stuID) {
		if (stuID == "" || stuID.contains(" ")) {
			System.out.println("[SPACE] and Empty StuID Not Allowed");
			return false;
		}
		if (searchStudent(stuID) != null) {
			System.out.println("Duplicate StuID Detected");
			return false;
		}
		stus.add(new Student(stuID));
		return true;
	}

	Student searchStudent(String stuID) {
		for (Student stu : stus) {
			if (stu.getStuID().equals(stuID))
				return stu;
		}
		return null;
	}

	ArrayList<Student> getStudentList() {
		return stus;
	}

	/**
	 * 列出一个学生当前已借图书
	 * @param stuID 学生ID
	 * @return 成功/失败(学生不存在/没有借书记录)
	 */
	boolean listStudentBorrowingBooks(String stuID) {
		Student stu = searchStudent(stuID);
		if (stu == null) {
			System.out.println("Student with Given stuID Not Found: " + stuID);
			return false;
		}
		if (stu.getBorrowingBooksNum() == 0) {
			System.out.println("This Student Doesn't Have Borrowing Books. Nothing to Show.");
			return false;
		}
		Set<Integer> borrowingBooks = stu.allBorrowingBooksID();
		System.out.println("ID\tBorrow Time\t\tName\tPress\tAuthor");
		System.out.println("------------------------------------------------------");
		for (Integer i : borrowingBooks) {
			Book b = searchID(i);
			System.out.print(b.getID() + "\t" + stu.getBorrowTime(b.getID()) + "\t");
			b.println();
		}
		return true;
	}

	/**
	 * 将图书表示为一个StringBuilder
	 * 格式如下:
	 * [ID]\t[余量]\t[总量]\t[书名]\t[出版社]\t[作者名]\n
	 * @return 保存了所有图书信息的StringBuilder
	 */
	StringBuilder toStringBuilder() {
		StringBuilder res = new StringBuilder();
		for (Book b : books.keySet()) {
			res.append(b.getID());
			res.append("\t");
			res.append(books.get(b));
			res.append("\t");
			res.append(b.getTotalNum());
			res.append("\t");
			res.append(b.getName());
			res.append("\t");
			res.append(b.getPress());
			res.append("\t");
			res.append(b.getAuthor());
			res.append("\n");
		}
		return res;
	}

	/**
	 * 导出到文件系统
	 * @param cmd Scanner
	 * @param key 密钥
	 * @return 成功/失败
	 * @throws IOException 遇到IO错误
	 */
	boolean exportToFileSystem(Scanner cmd, int key) throws IOException {
		//开始导出图书信息
		BufferedWriter bw = new BufferedWriter(new FileWriter("book.libdata"));
		StringBuilder res = new StringBuilder("Senren*Banka\n");//校验用文件头, 类似魔数(Magical Number)
		res.append(toStringBuilder());
		char[] charArry = res.toString().toCharArray();//转换为char[]便于取异或加密

		for (int i = 0; i < charArry.length; i++) {
			charArry[i] = (char)(charArry[i]^key);//取异或加密
		}
		bw.write(charArry);//写入缓冲区
		bw.flush();//刷新
		bw.close();//关闭文件释放资源
		
		//开始导出学生信息,同上
		bw = new BufferedWriter(new FileWriter("stu.libdata"));
		res = new StringBuilder("YuzuSoft\n");
		for (Student stu : stus) {
			res.append("------\n");//每一位学生的记录之前用"------"分隔
			res.append(stu.getStuID() + "\n");
			for (Integer currBook : stu.allBorrowingBooksID()) {
				res.append(currBook + "\t");
				res.append(stu.getBorrowTime(currBook) + "\n");
			}
		}
		charArry = res.toString().toCharArray();
		for (int i = 0; i < charArry.length; i++) {
			charArry[i] = (char)(charArry[i]^key);
		}
		bw.write(charArry);
		bw.flush();
		bw.close();
		return true;
	}

	/**
	 * 尝试从文件系统导入
	 * @param in Scanner
	 * @param key 密钥
	 * @return 成功/失败
	 * @throws IOException
	 */
	boolean importFromFileSystem(Scanner in, int key) throws IOException {
		File bookFile = new File("book.libdata");
		File stuFile = new File("stu.libdata");
		String currLine;
		char[] charArray;
		if (stuFile.exists() == false || bookFile.exists() == false) {//文件之一不存在
			System.out.println("Data File Not Found.");
			return false;
		}

		//开始导入图书数据
		BufferedReader br = new BufferedReader(new FileReader("book.libdata"));
		FileInputStream bookInputStream = new FileInputStream(bookFile);
		byte[] bytes = new byte[(int) bookFile.length()];
		int ret = bookInputStream.read(bytes);
		String line = new String(bytes, 0, ret);//这里line包含了文件的所有内容
		StringBuilder res = new StringBuilder();
		charArray = line.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = (char) (charArray[i] ^ key);//解密
			res.append(c);
		}
		String[] resStringArray = res.toString().split("\n");// 每一行结果
		String[] resArray;// 每一行的拆分结果
		if (resStringArray[0].equals("Senren*Banka") == false) {//文件头校验失败, 可能是密钥错误或者文件损坏
			System.out.println("Password Incorrect or Data File Courrupted");
			br.close();
			bookInputStream.close();
			return false;
		}
		for (int i = 1; i < resStringArray.length; i++) {
			currLine = resStringArray[i];
			resArray = currLine.split("\t");//每一行按照 \t 拆分
			ArrayList<String> author = new ArrayList<>(//对于作者的信息,由于ArrayList.toString()返回的是类似[a,b,c,d]这样的结果,需要去掉括号"[" "]"再按照","拆分
					Arrays.asList(resArray[5].replace("[", "").replace("]", "").split(",")));
			Book newBook = new Book(resArray[3], author, resArray[4], Integer.parseInt(resArray[2]));//导入的新书
			newBook.setID(Integer.parseInt(resArray[0]));
			Integer remainNum = Integer.parseInt(resArray[1]);
			books.put(newBook, remainNum);//保存到books里
		}
		typeOfBooks = resStringArray.length - 1;//总图书种类
		br.close();

		//开始导入学生数据,相同之处不再赘述
		BufferedReader sr = new BufferedReader(new FileReader("stu.libdata"));
		FileInputStream stuInputStream = new FileInputStream(stuFile);
		bytes = new byte[(int) stuFile.length()];
		ret = stuInputStream.read(bytes);
		line = new String(bytes, 0, ret);
		res = new StringBuilder();
		charArray = line.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = (char) (charArray[i] ^ key);
			res.append(c);
		}
		resStringArray = res.toString().split("\n");// 每一行结果
		if (resStringArray[0].equals("YuzuSoft") == false) {
			System.out.println("Password Incorrect or Data File Courrupted");
			sr.close();
			bookInputStream.close();
			stuInputStream.close();
			return false;
		}
		Student currStu = null;
		for (int i = 1; i < resStringArray.length; i++) {
			if (resStringArray[i].equals("------")) {//学生记录开头
				i++;//下移一行
				String currStuID = resStringArray[i];//获得学号
				i++;//下移一行
				currStu = new Student(currStuID);//新建一个学生
				for (Student stu : stus) {//如果已经添加过这个学生的话,只需要把剩下的记录导入就行了
					if (stu.getStuID().equals(currStuID)) {
						currStu = stu;
						break;
					}
				}
				resArray = resStringArray[i].split("\t");//将每一行按照\t分割
				if (currStu.addToBorrowingBooks(Integer.parseInt(resArray[0]), resArray[1]) == false) {
					System.out.println("Error Occured while Importing Student Data. Abort.");
					sr.close();
					stuInputStream.close();
					bookInputStream.close();
					return false;
				}
				stus.add(currStu);//是开头的话那就新加上这个学生
			} else {
				resArray = resStringArray[i].split("\t");
				if (currStu.addToBorrowingBooks(Integer.parseInt(resArray[0]), resArray[1]) == false) {
					System.out.println("Error Occured while Importing Student Data. Abort.");
					sr.close();
					stuInputStream.close();
					bookInputStream.close();
					return false;
				}
			}

		}
		//关闭各个文件释放资源
		sr.close();
		bookInputStream.close();
		stuInputStream.close();
		return true;
	}
}
