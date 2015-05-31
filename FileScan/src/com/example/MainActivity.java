package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.example.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	Stack<String> stack;
	ListView listView;
	File rootFile;
	File listFile[];
	int image_type[];
	String text_name[];
	String[] fileTypes=new String[]{"apk","avi","bat","bin","bmp","chm","css","dat","dll","doc","docx","dos","dvd","gif","html","ifo","inf","iso"
			,"java","jpeg","jpg","log","m4a","mid","mov","movie","mp2","mp2v","mp3","mp4","mpe","mpeg","mpg","pdf","php","png","ppt","pptx","psd","rar","tif","ttf"
			,"txt","wav","wma","wmv","xls","xlsx","xml","xsl","zip"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stack = new Stack<String>();
		rootFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		initListView(rootFile);            //初始化ListView
		stack.add(rootFile.getPath());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				File clFile = listFile[arg2];
				if(clFile.isDirectory()){
					initListView(clFile);
					stack.add(clFile.getPath());
				}
				else
					openFile(clFile);
			}
		});
	
	}
	/**
	 * 打开文件
	 * @param file
	 */
	private void openFile(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getFileType(file);
		intent.setDataAndType(Uri.fromFile(file),type);
	    startActivity(intent); 
	}
	
	/**
	 * 获取文件类型
	 */
	private String getFileType(File file){
		 String type="";
	      String fName=file.getName();
	      String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
	      
	      if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||end.equals("xmf")||end.equals("ogg")||end.equals("wav"))	      
	    	  type = "audio"; 
	      else if(end.equals("3gp")||end.equals("mp4"))
	    	  type = "video";
	      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||end.equals("jpeg")||end.equals("bmp")) 
	    	  type = "image";
	      else if(end.equals("apk")) 
	    	  type = "application/vnd.android.package-archive"; 
	      else if(end.equals("txt")||end.equals("java")) 
	    	  type = "text";
	      else
	    	  type="*";
	   
	      if(end.equals("apk")) {}
	      else 
	        type += "/*";  
	      return type;  
	}
	/**
	 * 初始化ListView
	 */
	private void initListView(File rootf){;
		listFile = rootf.listFiles();
		setTitle(rootf.getPath());
		image_type = new int[listFile.length];
		text_name = new String[listFile.length];
		listView = (ListView)findViewById(R.id.list);
		setTitleAndImage();
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		for(int i=0;i<listFile.length;i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("image_type", image_type[i]);
			map.put("text_name", text_name[i]);
			listItems.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,R.layout.item,new String[]{"text_name","image_type"},new int[]{R.id.text_name,R.id.image_type});
		listView.setAdapter(adapter);
	}
	private void setTitleAndImage(){
		
		ArrayList<File> tempFolder=new ArrayList<File>();
    	ArrayList<File> tempFile=new ArrayList<File>();
    	for(int i=0;i<listFile.length;i++){
    		File file=listFile[i];
    		if(file.isDirectory()){
    			tempFolder.add(file);
    		}else if(file.isFile()){
    			tempFile.add(file);
    		}
    	}
    	//对List进行排序
    	Comparator<File> comparator=new MyComparator(); 
    	Collections.sort(tempFolder, comparator);
    	Collections.sort(tempFile, comparator);
    	File datas[]=new File[tempFolder.size()+tempFile.size()];
    	System.arraycopy(tempFolder.toArray(), 0, datas, 0, tempFolder.size());
    	System.arraycopy(tempFile.toArray(), 0, datas,tempFolder.size(), tempFile.size());
		for(int i=0;i<datas.length;i++){
			
			image_type[i]= getImageType(datas[i]);
			text_name[i] = datas[i].getName();
		}
		listFile=datas;
	}
	/******************************************************/
	class MyComparator implements Comparator<File>{
		@Override
		public int compare(File lhs, File rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
    	
    }
    /**************************************************/
	private int getImageType(File file){
		if(file.isDirectory()){
			return R.drawable.folder;
		}
		else{
			int pointIndex=file.getName().lastIndexOf(".");
			if(pointIndex!=-1){
				String type = file.getName().substring(pointIndex+1).toLowerCase();
				for(int i=0;i<fileTypes.length;i++){
					if(type.equals(fileTypes[i])){
						try{
							int resId = getResources().getIdentifier(type, "drawable", getPackageName());
							return resId;
						}catch(Exception e){}
					}
				}
			}
		}
		return R.drawable.file;
	}
	/****************对“返回键”响应********************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(stack.size()>1){
				stack.pop();
				initListView(new File(stack.peek()));
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	/******************************************************/
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//    	menu.add("退出");
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		if(item.getTitle().toString().equals("退出")){
//			MainActivity.this.finish();
//			System.exit(1);
//	
//		}
//		return false;
//	}
}
