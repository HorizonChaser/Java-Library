import java.io.File;
import java.io.IOException;
import java.util.*;

public class DemoLibrary {
//总计1037行, 有史以来规模最大的屎山hhh
	/**
	 * 显示欢迎菜单
	 */
	static void showMenu() {
		System.out.println("┌----| Welcome to use DemoLibrary written by HorizonChaser |----┐");
		System.out.println("├---------------------------------------------------------------┤");
		System.out.println("|[NOTICE]Input the number ahead of the function as your choice  |");
		System.out.println("├---------------------------------------------------------------┤");
		System.out.println("|          1. Add books to the library                          |");
		System.out.println("|          2. Change the total num of a existing book           |");
		System.out.println("|          3. List all existing books                           |");
		System.out.println("|          4. Borrow a book as a student                        |");
		System.out.println("|          5. Return a book you are borrowing                   |");
		System.out.println("|          6. Search a book by name                             |");
		System.out.println("|          7. Search a book by one of it's author               |");
		System.out.println("|          8. Search a book by it's press                       |");
		System.out.println("|          9. Register a student into the library               |");
		System.out.println("|          10.List books a student has borrowed                 |");
		System.out.println("|          11.Delete a Book with its ID                         |");
		System.out.println("|          12.Import/Export data via file system                |");
		System.out.println("|          13.Exit the library                                  |");
		System.out.println("└---------------------------------------------------------------┘");
		System.out.print("Please input your choice: ");
	}

	/**
	 * 暂停控制台窗口,以免输出被覆盖
	 * 
	 * @param in 输入流
	 */
	static void pauseConsole(Scanner in) {
		System.out.println("[Press ENTER key to continue...]");
		try {
			System.in.read();
		} catch (Exception e) {
		}
	}

	/**
	 * 退出并致谢
	 * 
	 * @param in 要结束的输入流
	 */
	static void exitDemoLibrary(Scanner in) {
		System.out.println("");
		System.out.println("------------------------| Acknowledgement |------------------------");
		System.out.println("Thanks for using this DemoLibrary application written by HorizonChaser");
		System.out.println("Powered by Java 13, Eclipse 2019R, Win10 Prof.1909 and Expectation(期望)");
		System.out.println("Institude of C.S.    Horizon Chaser");
		System.out.println("Finished at 2020/04/17");
		in.close();
		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		Scanner cmd = new Scanner(System.in);
		String input = "";
		String cmdArray[];
		List plants = new ArrayList();
		int choice = 0;
		ArrayList<Book> searchResult;
		Librarian lib = new Librarian();// 图书馆实例

		File bookFile = new File("book.libdata");
		File stuFile = new File("stu.libdata");
		if (bookFile.exists() && stuFile.exists()) {
			System.out.println("Data File Exists. Import Now?(Y/N)");
			input = cmd.nextLine();
			if (input.toUpperCase().equals("Y")) {
				System.out.println("Please Enter Your Key:");
				int keyForImport = Integer.MAX_VALUE;
				try {
					keyForImport = Integer.parseInt(cmd.nextLine());
				} catch (NumberFormatException e) {
					System.out.println("Illegal Key Input");
				}
				if (keyForImport != Integer.MAX_VALUE) {
					if (lib.importFromFileSystem(cmd, keyForImport)) {
						System.out.println("Data Successfully Imported");
					} else {
						System.out.println("Data Failed to Import");
					}
					pauseConsole(cmd);
				}
			}
		}

		while (true) {
			showMenu();
			input = "";
			searchResult = new ArrayList<>();
			choice = 0;
			do {
				// 在pauseConsole()方法中,按下回车键继续后会使得Scanner cmd 获取到一个空串"",进而引发无尽的Invalid command
				input = cmd.nextLine();
			} while (input.equals(""));// 所以我们直到获取到一个非空串再继续
			cmdArray = input.split(" ");// 空格分隔输入的各个参数
			try {
				choice = Integer.parseInt(cmdArray[0]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input: " + cmdArray[0]);
				pauseConsole(cmd);
				choice = 99;
			}

			switch (choice) {
			case 1:
				String newName = "", newPress = "", newAuthor = "";
				int totalNum = 0;
				ArrayList<String> newAuthorList = new ArrayList<>();
				System.out.println("Please input the name of the book");
				do {
					newName = cmd.nextLine();
				} while (newName.equals(""));
				System.out.println("Please input the author, split in [COMMA] - \",\" if needed");
				do {
					newAuthor = cmd.nextLine();
					cmdArray = newAuthor.split(",");
				} while (cmdArray[0].equals(""));
				HashSet<String> checkAuthor = new HashSet<>(Arrays.asList(cmdArray));// 用来去重, 避免一本书中出现重复的作者
				if (checkAuthor.size() != cmdArray.length) {
					System.out.println("Duplicate Author Detected, and they have been removed");
				}
				for (String author : checkAuthor) {
					newAuthorList.add(author);
				}
				System.out.println("Please input the press");
				do {
					newPress = cmd.nextLine();
				} while (newPress.equals(""));
				System.out.println("Please input the total num of the book");
				try {
					totalNum = Integer.parseInt(cmd.nextLine());
				} catch (NumberFormatException e) {
					System.out.println("Invalid Amount");
					break;
				}
				Book newBook = new Book(newName, newAuthorList, newPress, totalNum);
				if (lib.isExist(newBook) != null) {// 这本书已经存在了,询问是否需要修改数量
					System.out.println("This book already exists. Add the amount to the existing one?(Y/N)");
					String dupConfirm = cmd.nextLine();
					switch (dupConfirm.toUpperCase()) {
					case "Y":
						lib.addBooks(newBook, totalNum);
						break;
					case "N":
						System.out.println("Book Insertion Aborted by User");
						break;
					default:
						System.out.println("Unrecognized Input:" + dupConfirm);
						break;
					}
				} else {
					lib.addBooks(newBook, totalNum);
				}
				break;

			case 2:
				int inputExpectedID = 0;
				System.out.print("Please input the id of the book you want to change:");
				String inputExpectedIDString = cmd.nextLine();
				try {
					inputExpectedID = Integer.parseInt(inputExpectedIDString);
				} catch (NumberFormatException e) {
					System.out.println("Invalid ID input: " + inputExpectedIDString);
					break;
				}
				Book numChangeBook = lib.searchID(inputExpectedID);
				if (numChangeBook == null) {
					System.out.println("Book with Given ID Not Found");
					break;
				}
				int inputExpectedNumChange = 0;
				System.out.print("Please input the amount you want to change:");
				String inputExpectedNumChangeString = cmd.nextLine();
				try {
					inputExpectedNumChange = Integer.parseInt(inputExpectedNumChangeString);
					lib.addBooks(numChangeBook, inputExpectedNumChange);
				} catch (NumberFormatException e) {
					System.out.println("Invalid Amount Input: " + inputExpectedNumChangeString);
					break;
				}
				break;

			case 3:
				if (lib.getTypeOfBooks() == 1) {
					System.out.println("No Existing Books. Nothing to Show...");
					break;
				}
				System.out.println("ID\tRemain\tTotal\tName\tPress\tAuthor");
				System.out.println("----------------------------------------------");
				for (int i = 1; i <= lib.getTypeOfBooks(); i++) {
					Book b = lib.searchID(i);
					if (b == null)
						continue;
					System.out.print(b.getID() + "\t");
					System.out.print(lib.getRemainNum(i) + "\t");
					System.out.print(b.getTotalNum() + "\t");
					b.println();
				}
				break;

			case 4:
				String inputStuID, inputBookID;
				System.out.print("Please Input Your Student ID: ");
				inputStuID = cmd.nextLine();
				System.out.print("Please Input ID of the Book ID You Want to Borrow:  ");
				inputBookID = cmd.nextLine();
				int inputBookIDinInt;
				inputBookIDinInt = 0;
				try {
					inputBookIDinInt = Integer.parseInt(inputBookID);
				} catch (NumberFormatException e) {
					System.out.println("Invalid Book ID: " + inputBookID);
					break;
				}
				if (lib.lentOutBook(inputBookIDinInt, inputStuID)) {
					System.out.println("Book Successfully Lent out");
				} else {
					System.out.println("Book Falied to Lent out");
				}
				break;

			case 5:
				System.out.print("Please Input Your StuID: ");
				String inputStuID_CASE5 = cmd.nextLine();
				if (lib.listStudentBorrowingBooks(inputStuID_CASE5) == false) {
					break;
				}
				System.out.print("Please Input ID of the Book You Want to Return: ");
				String inputBookID_CASE5 = cmd.nextLine();
				int inputBookIDinInt_CASE5;
				inputBookIDinInt_CASE5 = 0;
				try {
					inputBookIDinInt_CASE5 = Integer.parseInt(inputBookID_CASE5);
				} catch (NumberFormatException e) {
					System.out.println("Invalid Book ID: " + inputBookID_CASE5);
					break;
				}
				if (lib.getReturnBook(inputBookIDinInt_CASE5, inputStuID_CASE5) == false) {
					System.out.println("Failed to Return Book to the Library");
				}
				break;

			case 6:
				System.out.println("Please Input the Name of the Book You Want to Search");
				input = cmd.nextLine();
				searchResult = lib.searchName(input);
				if (searchResult.size() == 0) {
					System.out.println("Book with Given Name Not Found: " + input);
					break;
				}
				System.out.println("ID\tRemain\tTotal\tName\tPress\tAuthor");
				System.out.println("----------------------------------------------");
				for (int i = 0; i < searchResult.size(); i++) {
					Book b = searchResult.get(i);
					if (b == null)
						break;
					System.out.print(b.getID() + "\t");
					System.out.print(lib.getRemainNum(b.getID()) + "\t");
					System.out.print(b.getTotalNum() + "\t");
					b.println();
				}
				break;

			case 7:
				System.out.println("Please Input one of the Authors of the Book You Want to Search");
				input = cmd.nextLine();
				searchResult = lib.searchAuthor(input);
				if (searchResult.size() == 0) {
					System.out.println("Book with Given Author Not Found: " + input);
					break;
				}
				System.out.println("ID\tRemain\tTotal\tName\tPress\tAuthor");
				System.out.println("----------------------------------------------");
				for (int i = 0; i < searchResult.size(); i++) {
					Book b = searchResult.get(i);
					if (b == null)
						break;
					System.out.print(b.getID() + "\t");
					System.out.print(lib.getRemainNum(b.getID()) + "\t");
					System.out.print(b.getTotalNum() + "\t");
					b.println();
				}
				break;

			case 8:
				System.out.println("Please Input the Press of the Book You Want to Search");
				input = cmd.nextLine();
				searchResult = lib.searchPress(input);
				if (searchResult.size() == 0) {
					System.out.println("Book with Given Press Not Found: " + input);
					break;
				}
				System.out.println("ID\tRemain\tTotal\tName\tPress\tAuthor");
				System.out.println("----------------------------------------------");
				for (int i = 0; i < searchResult.size(); i++) {
					Book b = searchResult.get(i);
					if (b == null)
						break;
					System.out.print(b.getID() + "\t");
					System.out.print(lib.getRemainNum(b.getID()) + "\t");
					System.out.print(b.getTotalNum() + "\t");
					b.println();
				}
				break;

			case 9:
				System.out.print("Please Input Your StuID, [SPACE] NOT ALLOWED: ");
				input = cmd.nextLine();
				if (lib.registerStudent(input) == false) {
					System.out.println("Registration Falied");
					break;
				}
				System.out.println("Registration Succeeded with StuID: " + input);
				break;

			case 10:
				System.out.print("Please Input Your Student ID: ");
				String inputStuID_CASE10 = cmd.nextLine();
				lib.listStudentBorrowingBooks(inputStuID_CASE10);
				break;

			case 11:
				System.out.print("Please Input ID of the Book You Want to Delete: ");
				input = cmd.nextLine();
				int bookID_CASE11 = 0;
				try {
					bookID_CASE11 = Integer.parseInt(input);
				} catch (NumberFormatException e) {
					System.out.println("Invalid BookID: " + input);
				}
				if (lib.deleteBooks(bookID_CASE11) == false) {
					System.out.println("Delete Process Failed");
					break;
				}
				break;

			case 12:
				System.out.println("To Import From File System, Enter I;To Export To File System, Enter E");
				input = cmd.nextLine();
				if (input.toUpperCase().equals("E") == false && input.toUpperCase().equals("I") == false) {
					System.out.println("Unrecognized Invalid Input: " + input);
					break;
				}
				System.out.println("Please Input Numeric Password to Protect the Data File, [SPACE] NOT ALLOWED: ");
				String password = cmd.nextLine();
				if (password.contains(" ")) {
					System.out.println("Invalid Password: " + password);
					break;
				}
				int key = 0;
				try {
					key = Integer.parseInt(password);
				} catch (NumberFormatException e1) {
					System.out.println("Invalid Password: " + password);
					break;
				}
				try {
					if (input.toUpperCase().equals("E")) {
						lib.exportToFileSystem(cmd, key);
					} else {
						lib.importFromFileSystem(cmd, key);
					}
				} catch (IOException e) {
					System.out.println("IO Exception Occurred. Check Your Device...");
					e.printStackTrace();
					break;
				}

				break;
			case 13:
				exitDemoLibrary(cmd);
				break;

			default:
				System.out.println("Unrecognized Invalid Input: " + input);// 未识别的无效指令
				break;
			}
			pauseConsole(cmd);
		}

	}

}
