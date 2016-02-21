package com.lcpuppets.TagExtract;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class Main {
	static String FINAL_HTML_INSERT = "";
	static ArrayList<String> divs;
	public static void main(String[] args) {
		File current = new File(Paths.get(".").toAbsolutePath().normalize().toString());
		ArrayList<String> names = new ArrayList<>();
		HashMap<String, String[]> imgtags = new HashMap<>();
		ArrayList<String> tags = new ArrayList<>();
		for (File f : current.listFiles()){
			if (f.isDirectory())continue;
			names.add(f.getName());
			System.out.println(f.getName());
		}
		
		for (File f : current.listFiles()){
			if (f.getName().endsWith(".txt") || f.getName().endsWith(".jar") || f.getName().endsWith(".bat") || f.getName().endsWith(".html") || f.isDirectory()){
				continue;
			}
			if (names.contains(f.getName().substring(0, f.getName().lastIndexOf(".") +1) + ".txt")){
				continue;
			}
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(f);
				for (Directory dir : metadata.getDirectories()){
					if(dir.getDescription(ExifSubIFDDirectory.TAG_WIN_KEYWORDS) != null){
						//createFile(f.getName().substring(0, f.getName().lastIndexOf(".") +1) + ".txt", dir.getDescription(ExifSubIFDDirectory.TAG_WIN_KEYWORDS));
						String[] splitTags = dir.getDescription(ExifSubIFDDirectory.TAG_WIN_KEYWORDS).split(";");
						imgtags.put(f.getName(), splitTags);
						for (String s : splitTags){
							if (!tags.contains(s)){
								tags.add(s);
							}
						}
					}
				}
				
			} catch (ImageProcessingException | IOException e) {
				e.printStackTrace();
			}
			
		}
		System.out.println(names.size());
		for (String s : names){
			if (s.endsWith("txt") || s.endsWith("html") || s.endsWith("bat") || s.endsWith("png") || s.endsWith("jar")){
				continue;
			}
			String html = div  +"";
			String[] sss = imgtags.get(s);
			StringBuilder sb = new StringBuilder();
			if (sss != null){
				for (String s2 : sss){
					sb.append(s2 + " ");
				}
			}
			//System.out.println(sb.toString());
			html = html.replace("$TAG$", "All " + sb.toString());
			html = html.replace("$IMAGE$", "images/" +s).replace("$IMAGE$", "images/" +s);
			//System.out.println(html);
			FINAL_HTML_INSERT += html;
		}
//		System.out.println(FINAL_HTML_INSERT);
		//createFile("test.txt", FINAL_HTML_INSERT);
		
		@SuppressWarnings("resource")
		String content = new Scanner(Main.class.getResourceAsStream("portfolio.html")).useDelimiter("\\Z").next();
		content = content.replace("$DIV_FINAL_INSERT$", FINAL_HTML_INSERT);
		String html = "";
		html+="<li><a href=\"#\" data-filter=\".All\">All</a></li> ";
		for (String s : tags){
			html += htmlTag.replace("$NAME$",s).replace("$NAME$", s);
		}
		content = content.replace("$TAG_DECLARATION$", html);
		//System.out.println(content);
		createFile("portfolio.html", content);
	}
	
	private static void createFile(String name, String contents){
		//System.out.println(FINAL_HTML_INSERT);
		//String stupidthingthatisntgoingtowork = contents;
		
		File f = new File(Paths.get(".").toAbsolutePath().normalize().toString(), name);
		if (!f.exists()){
			try {
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeBytes(contents);
				//dos.writeUTF(contents);
				
				//System.out.println(contents);
				//dos.write(contents.getBytes());
				dos.flush();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//$IMAGE$
	//$TAG$
private static String div = "<div class=\"isotope-item $TAG$\" itemscope itemtype=\"http://schema.org/CreativeWork\">\n" +

						"<div class=\"thumb_img\">\n<a href=\"project.html?image=$IMAGE$\" title=\"Project\" rel=\"bookmark\" itemprop=\"url\">\n" +
							"<img src=\"$IMAGE$\" alt=\"View Project\" itemprop=\"image\">\n" +
							"<div class=\"overlay\">\n" +
								"<span class=\"title\" itemprop=\"name\">Project Name</span>\n" +
							"</div> <!-- .overlay -->\n" +
						"</a>\n</div>\n" +

					"</div> <!-- .isotope-item -->\n\n\n";
private static String htmlTag = "<li><a href=\"#\" data-filter=\".$NAME$\">$NAME$</a></li> ";
					/*<div class="isotope-item web" itemscope itemtype="http://schema.org/CreativeWork">

						<a href="project.html" title="Project" rel="bookmark" itemprop="url">
							<img src="http://dummyimage.com/540x500/f3f3f3/d1d1d1.jpg&text=Thumbnail" alt="View Project" itemprop="image">
							<div class="overlay">
								<span class="title" itemprop="name">Project Name</span>
							</div> <!-- .overlay -->
						</a>

					</div> <!-- .isotope-item -->
					*/
}
