import java.io.*;
import java.util.*;

public class FileManage {
    Map<String, String> category = new HashMap<>();
    List<Product> productList = new ArrayList<>();
    List<Member> membersList = new ArrayList<>();
    List<MemberData> memberData = new ArrayList<>();
    int memberNumber;

    public void writeFile(String newLine, File file) throws IOException {
        BufferedWriter writeFile = new BufferedWriter(new FileWriter(file, true));
        writeFile.write(newLine);
        writeFile.close();
    }

    //Method แสดงรายการสินค้าทั้งหมด | รับไฟล์
    public void showCategory(File file) throws IOException {
        BufferedReader readFile = new BufferedReader(new FileReader(file));

        //ตัวแปรเอาไว้นับจำนวนรายการสินค้าทั้งหมด
        int count = 1;

        //อ่านไฟล์ category
        String line;
        while ((line = readFile.readLine()) != null){
            String[] data = line.split("\\t");
            category.put(String.valueOf(count), data[1].trim());
            count++;
        }

        System.out.println("===== SE STORE's Product Categories =====");
        System.out.printf("%-7s %-15s \n", "#", "Category");
        for (String i : category.keySet()){
            System.out.printf("%-7s %-15s \n", i, category.get(i));
        }
        System.out.println("=========================================");
        readFile.close();
    }

    public void loadProduct(File file) throws FileNotFoundException {
        Scanner readFile = new Scanner(file);
        while (readFile.hasNext()) {
            Product product = new Product();
            product.setAmount(readFile.next().trim());
            product.setName(readFile.next().trim());
            product.setPrice(readFile.next().trim());
            product.setQuantity(readFile.next().trim());
            product.setId(readFile.next().trim());
            productList.add(product);
        }
    }

    //Method แสดงรายการสินค้าที่เราเลือก | รับไฟล์, รับตัวเลือกที่เลือก
    public void showProductSelect(File file, String selectType) throws FileNotFoundException {
        Scanner readFile = new Scanner(file);
        //ตัวแปรไว้นับจำนวนรายการที่แสดง
        int countProduct = 1;

        //ลูปเอาไว้เช็คข้อมูลของ Product บรรทัดไหนบ้างที่ตรงกับ Category ที่เลือก
        if (productList.isEmpty()) {
            while (readFile.hasNext()) {
                Product product = new Product();
                product.setAmount(readFile.next().trim());
                product.setName(readFile.next().trim());
                product.setPrice(readFile.next().trim());
                product.setQuantity(readFile.next().trim());
                product.setId(readFile.next().trim());
                productList.add(product);
            }
        }

        String checkTypeId = "";
        for (String i : category.keySet()){
            checkTypeId = i.equals(selectType) ? String.valueOf(((Integer.parseInt(i)-1)+3000)) : "" ;
            if (!checkTypeId.isEmpty()) break;
        }

        double discount = 0;
        if (checkRole().equalsIgnoreCase("2")){
            discount = 0.05;
        }else if (checkRole().equalsIgnoreCase("3")){
            discount = 0.10;
        }


        System.out.println("============ " + category.get(selectType) + " ============");
        String title = discount != 0 ? String.format("%-5s %-17s %-12s %14s \n", "#" ,"Name", "Price (฿)", "Quantity")
                : String.format("%-5s %-15s %-10s %6s \n", "#" ,"Name", "Price (฿)", "Quantity");
        System.out.print(title);
        for (Product i : productList){
            if (i.getId().equals(checkTypeId)){
                double price = Double.parseDouble(i.getPrice().substring(1)) * 34;
                double disPrice = price - (price * discount);
                String printPrice = String.format("%.2f", price);
                String displayProduct = discount != 0 ? String.format("%-5d %-15s %-7.2f %-15s %s\n", countProduct, i.getName(), disPrice, "("+printPrice+")", i.getQuantity())
                        : String.format("%-5s %-17s %-8.2f %5s \n", countProduct, i.getName(), price, i.getQuantity());
                System.out.print(displayProduct);
                countProduct++;
            }
        }

        System.out.println("================================");
    }

    public void sortDESC(){
        Product temp;
        int min;
        for (int i = 0; i < productList.size()-1; i++) {
            min = i;
            temp = productList.get(i);
            for (int j = i+1; j < productList.size(); j++) {
                if (productList.get(min).getName().compareTo(productList.get(j).getName()) < 0){
                    min = j;
                }
            }
            productList.set(i, productList.get(min));
            productList.set(min, temp);
        }
    }

    public void sortASC(){
        Product temp;
        int min;
        for (int i = 0; i < productList.size()-1; i++) {
            min = i;
            temp = productList.get(i);
            for (int j = i+1; j < productList.size(); j++) {
                if (Integer.parseInt(productList.get(min).getQuantity()) > Integer.parseInt(productList.get(j).getQuantity())){
                    min = j;
                }
            }
            productList.set(i, productList.get(min));
            productList.set(min, temp);
        }
    }

    //Method เอาไว้นับจำนวนรายการสินค้า | รับไฟล์
    public int getCountProduct() {
        return category.size();
    }

    //Method เอาไว้เช็คว่า Email กับ Password ถูกมั้ย | รับไฟล์, email, password
    public boolean checkCorrect(File file, String Email, String Password) throws FileNotFoundException {
        Scanner readFile = new Scanner(file);
        membersList.clear();

        while (readFile.hasNext()){
            Member member = new Member();

            member.setId(readFile.next().trim());
            member.setFirstName(readFile.next().trim());
            member.setLastName(readFile.next().trim());
            member.setEmail(readFile.next().trim());
            member.setPassCode(readFile.next().trim());
            member.setPhoneNum(readFile.next().trim());
            member.setPoint(readFile.next().trim());

            StringBuilder pass = new StringBuilder();
            pass.append(member.getPassCode().charAt(9));//เพื่อค่า passCode ตำแหน่งที่ 9 .append ฟีลเหมือน pass += passCode.charAt(9)
            pass.append(member.getPassCode().charAt(10));
            pass.append(member.getPassCode().charAt(13));
            pass.append(member.getPassCode().charAt(14));
            pass.append(member.getPassCode().charAt(15));
            pass.append(member.getPassCode().charAt(16));

            if (Password.equals(String.valueOf(pass)) && Email.equals(member.getEmail())){//เช็คว่า email กับ password ตรงกับ ข้อมูลในไฟล์ มั้ย
                membersList.add(member); //เก็บ email ที่ login ผ่านไว้
                readFile.reset();
                return true;
            }
        }
        return false;
    }

    //Method เอาไว้เช็คว่า Email กับ Password ถูกมั้ย
    public boolean checkExpired() {
        //ตัวแปรเอาไว้เก็บว่า บัญชีหมดอายุมั้ย ถ้าหมดเป็น false ถ้ายังไม่หมดเป็น true
        boolean check = false;

        for (Member i : membersList) {
            check = i.passCode.charAt(2) == '1';
        }

        return check;
    }

    //Method เอาไว้แสดงข้อมูล member
    public void showDataMember() {
        //ตัวแปรเอาไว้เก็บข้อมูลที่ดัดแปลงแล้ว เพื่อเอาไปแสดง | **สังเกตุความแตกต่างที่พิมพ์ใหญ่ตัวแรก**
        StringBuilder FirstName = new StringBuilder();
        StringBuilder FullName = new StringBuilder("Hello, ");
        StringBuilder Email = new StringBuilder();
        StringBuilder Phone = new StringBuilder();
        StringBuilder Point = new StringBuilder();

        String role = "";

        for (Member i : membersList){
            //ชื่อ
            FirstName.append(i.getLastName().charAt(0));//เอานามสกุลตัวแรก
            FirstName.append(".");//เพิ่มจุด

            if (i.getPassCode().charAt(6) == '0'){
                role = "STAFF";
            }if(i.getPassCode().charAt(6) == '1'){
                role = "REGULAR";
            }if (i.getPassCode().charAt(6) == '2'){
                role = "SILVER";
            }if (i.getPassCode().charAt(6) == '3'){
                role = "GOLD";
            }

            FullName.append(FirstName).append(" ").append(i.getFirstName()).append(" (").append(role).append(")");//เอาชื่อมารวมกัน

            //email
            Email.append(i.getEmail());//เอา email ในไฟล์ไปใส่ในตัวแปรชนิด StringBuilder เพื่อง่ายต่อการดัดแปลง
            Email.replace(2, i.getEmail().indexOf("@"), "***");//แทนที่ ตัวแหน่งที่ 2 ถึง ตำแหน่งที่ @ อยู่ ด้วย ***
            Email.replace(Email.indexOf("@")+3, Email.length(), "***");//แทนที่ ตำแหน่งที่ @ อยู่ ถัดไป 3 ตัว ถึง ตำแหน่งสุดท้าย ด้วย ***

            //เบอร์
            Phone.append(i.getPhoneNum());
            Phone.insert(3,"-");//แทรก (เพิ่ม) - ในตำแหน่งที่ 3
            Phone.insert(7,"-");//แทรก (เพิ่ม) - ในตำแหน่งที่ 7

            //point
            Point.append(i.getPoint(), 0, i.getPoint().indexOf("."));//เอาตั้งแต่ตำแหน่งที่ 0 ไปจนถึงตำแหน่งก่อน ตำแหน่ง .
        }

        System.out.println("===== SE STORE =====");
        System.out.println(FullName);
        System.out.println("Email: " + Email);
        System.out.println("Phone: " + Phone);
        System.out.println("You have " + Point + " Point");

    }

    public String checkRole() {
        String role = "";

        for (Member i : membersList){
            role = String.valueOf(i.getPassCode().charAt(6));
        }

        return role;
    }

    public String randomPassword(){
        StringBuilder pass = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randomInt = random.nextInt(10);
            pass.append(randomInt);
        }
        return String.valueOf(pass);
    }

    public String randomChar(String pass){
        String character = "ABCDEFGHIJKLMNOPQRSPUVWUSYZ";
        StringBuilder passWord = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 19 ; i++) {
            int randomInt = random.nextInt(26);
            passWord.append(character.charAt(randomInt));
        }

        passWord.replace(2,3, "1");
        passWord.replace(6,7,"1");
        //passWord.setCharAt(9,pass.charAt(0));
        passWord.replace(9,10,String.valueOf(pass.charAt(0)));
        passWord.replace(10,11,String.valueOf(pass.charAt(1)));
        passWord.replace(13,14,String.valueOf(pass.charAt(2)));
        passWord.replace(14,15,String.valueOf(pass.charAt(3)));
        passWord.replace(15,16,String.valueOf(pass.charAt(4)));
        passWord.replace(16,17,String.valueOf(pass.charAt(5)));

        return String.valueOf(passWord);
    }

    public void loadMemberData(File file) throws FileNotFoundException {
        Scanner read = new Scanner(file);
        memberData.clear();
        while (read.hasNext()){
            MemberData data = new MemberData();
            data.setId(read.next().trim());
            data.setFirstName(read.next().trim());
            data.setLastName(read.next().trim());
            data.setEmail(read.next().trim());
            data.setPassCode(read.next().trim());
            data.setPhoneNum(read.next().trim());
            data.setPoint(read.next().trim());
            memberData.add(data);
        }
    }

    public void showMember(){
        int i = 0;
        System.out.println("===== SE STORE's Member =====");
        System.out.printf("%-5s %-25s %s\n", "#", "Name", "Email");

        for (MemberData data : memberData){
            i++;
            String fullName = data.getFirstName() + " " + data.getLastName();
            System.out.printf("%-5d %-25s %s\n",i, fullName, data.getEmail());
        }
        System.out.println("================================");
        System.out.println("Type Member Number, You want to edit or Press Q to Exit");
        System.out.print("Select (1-" + i + ") : ");
        memberNumber = i;
    }

    public int amountMember(){
        return memberNumber;
    }

    public void showNameEdit(String select){
        System.out.println("==== Edit info of " + memberData.get(Integer.parseInt(select)-1).getFirstName() + " " + memberData.get(Integer.parseInt(select)-1).getLastName() + " ====");
        System.out.println("Type new info or Hyphen (-) for none edit.");
    }

    public void editMember(File file, String select, String F, String L, String E, String P) throws IOException {
        if (!F.equals("-")){
            memberData.get(Integer.parseInt(select)-1).setFirstName(F);
        }
        if (!L.equals("-")){
            memberData.get(Integer.parseInt(select)-1).setLastName(L);
        }
        if (!E.equals("-")){
            memberData.get(Integer.parseInt(select)-1).setEmail(E);
        }
        if (!P.equals("-")){
            memberData.get(Integer.parseInt(select)-1).setPhoneNum(P);
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (MemberData i : memberData){
            writer.write(i.getId() + "\t" + i.getFirstName() + "\t" + i.getLastName() + "\t" + i.getEmail() + "\t" + i.getPassCode() + "\t" + i.getPhoneNum() + "\t" + i.getPoint());
            writer.newLine();
        }
        writer.close();
        System.out.println("Success - Member has been updated!");
    }


}
