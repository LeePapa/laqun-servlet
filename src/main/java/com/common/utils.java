package com.common;

import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class utils {
    public static Map<String, String> snHttpTimeMap = Collections.synchronizedMap(new HashMap<String, String>()); //sn访问接口的时间
    public static AtomicBoolean isGetAddWx = new AtomicBoolean(false); //是否正在从数据库获取addWx
    public static AtomicBoolean hasAddWx = new AtomicBoolean(true); //数据库是否有没用过的addWx
    public static List<String> addWxList = Collections.synchronizedList(new LinkedList<String>()); //addWx的列表
    public static String webPath = "";
    public static String tpUriPre = "-1258004048.cos.ap-guangzhou.myqcloud.com/";
    public static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTimeStr() {
        return sdf.format(new Date());
    }

    public static void wlToF(String fp, String s) {
        try {
            FileWriter fw = new FileWriter(fp);
            fw.append(s);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String rlFromF(String fp) {
        String s = "";
        String s2;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fp));
            s2 = s;
            while (true) {
                try {
                    String readL = br.readLine();
                    if (readL != null) {
                        s2 = s2 + readL;
                    } else {
                        br.close();
                        s = s2;
                        return s2;
                    }
                } catch (Exception e) {
                    return s2;
                }
            }
        } catch (Exception e2) {
            s2 = s;
            return s2;
        }
    }

    public static void rmFile(String fp) {
        File f = new File(fp);
        if (f.exists()) {
            f.delete();
        }
    }

    public static Connection getConnection() {
        return DbUtils.getConnect();
    }

    public static void byte2File(byte[] bArr, String fPath, ServletContext ctx) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fPath)));
            bos.write(bArr, 0, bArr.length);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            ctx.log(e.getMessage());
        }
    }

    public static Map getFormData(HttpServletRequest request) {
        Map map = new HashMap();
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setHeaderEncoding("UTF-8");
        List items = null;
        try {
            items = upload.parseRequest(request);
        } catch (Exception e) {
            System.out.println("parse formdata err: " + e.getMessage());
        }
        for (Object object : items) {
            FileItem fileItem = (FileItem) object;
            System.out.println(fileItem.getFieldName());
            map.put(fileItem.getFieldName(), object);
        }
        return map;
    }

    public static String[] txt2array(FileItem fi) {
        try {
            String s = fi.getString("utf-8");
            if (s.indexOf("\r\n") != -1) {
                return s.split("\r\n");
            }
            return s.split("\n");
        } catch (Exception e) {
            return new String[0];
        }
    }

    public static List generalRandomIntArr(int intNum, int maxNum) {
        Random r = new Random();
        List l = new ArrayList(intNum);
        int i = 0;
        while (i < intNum) {
            int rNum = r.nextInt(maxNum);
            if (l.contains(Integer.valueOf(rNum))) {
                i--;
            } else {
                l.add(Integer.valueOf(rNum));
            }
            i++;
        }
        return l;
    }

    public static void mkdir(String dirName) {
        File dirF = new File(webPath + "/" + dirName);
        if (dirF.exists()) {
            System.out.println(dirF + " is exists");
            return;
        }
        System.out.println("mkdir " + dirF);
        dirF.mkdirs();
    }

    public static void byteArr2File(byte[] bArr, String fPath) {
        File file = new File(fPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(bArr);
            bos.flush();
            bos.close();
            fos.close();
        }catch (Exception e) {

        }
    }

    public static String getExceptionMsg(Exception e){
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        }catch (Exception e1){
            return "获取exception详细信息发生错误： " + e.getMessage();
        }finally {
            if (sw!=null){
                try {
                    sw.close();
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
            if (pw != null){
                pw.close();
            }
        }
    }

}