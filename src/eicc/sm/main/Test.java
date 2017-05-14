package eicc.sm.main;

import java.util.Date;

public class Test {

	public static void main(String[] args) {
		StringBuffer buffer = new StringBuffer();
		StringBuffer buffer2 = new StringBuffer();
		buffer.append("RespondentID\t" + "Start_Date\t" + "End_Date\t" + "IP_Address\t");
		
		int x = 0;
		while(x < 9){
			x = x + 1;
			int y = 0;
			while(y < 2){
				y = y + 1;
				buffer.append(("Question"+x)+"\t");
				x = x + 1;
				int z = 0;
				
				while(z < 1){
					z = z + 1;
					
					if(x == 0){
						buffer2.append(z + "\t");
						buffer2.append(new Date().toString() + "\t");
						buffer2.append(new Date().toString() + "\t");
						buffer2.append("1.2.206.101" + "\t");
					}
					int p = 0;
					while(p < 1){
						p = p +1;
						int m = 0;
						while(m < 1){
							m = m + 1;
							buffer2.append((m+"_value"));
							buffer2.append("\n");
							System.out.println(buffer2.toString());
						}
					}
					
				}
				
			}
			
		}
		buffer.append("\n");
		
		System.out.println(buffer.toString());
		
	}

	
	
}
