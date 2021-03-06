package org.example.foodappmobile;

import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{
	
	private String name;
	private String description;
	private String price;
	private String quantity;
	private String stock_qty;
	private String stock_threshold;
	private String tot;
	
	public Product() {}
	
	public void setName( String name) {
		this.name = name;
	}
	
	public void setDescription( String description) {
		this.description = description;
	}
	
	public void setPrice (String price) {
		this.price = price;
	}
	
	public void setQuantity (String quantity) {
		this.quantity = quantity;
	}
	
	public void setStockQty (String stock_qty) {
		this.stock_qty = stock_qty;
	}
	
	public void setStockThreshold (String stock_threshold) {
		this.stock_threshold = stock_threshold;
	}
	
	public void setTot (String tot) {
		this.tot = tot;
	}
	
	public String getName() { return name;}
	
	public String getDescription() { return description;}
	
	public String getPrice() { return price; }
	
	public String getQuantity() { return quantity; }
	
	public String getStockQty() { return stock_qty; }
	
	public String getStockThreshold() { return stock_threshold; }
	
	public String getTot() { 
		int qty = Integer.parseInt(quantity);
		String pr = price;
		pr = pr.replace(" EUR", "");
		pr = pr.replace(",", ".");
		double p = Double.parseDouble(pr);
		double t = (qty * p);
		DecimalFormat twoDec = new DecimalFormat("0.00");
		tot = twoDec.format(t);
		tot = "" + tot;
		//tot = Double.toString(t);
		tot = tot.replace(".", ",");
		tot = tot + " EUR";
		return tot; 
	}
	
	public Product(Parcel in) {
		readFromParcel(in);
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
 
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(price);
		dest.writeString(quantity);
		dest.writeString(stock_qty);
		dest.writeString(stock_threshold);
	}
	
	private void readFromParcel(Parcel in) {
		 
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		name = in.readString();
		description = in.readString();
		price = in.readString();
		quantity = in.readString();
		stock_qty = in.readString();
		stock_threshold = in.readString();
	}
	
	public static final Parcelable.Creator<Product> CREATOR =
    	new Parcelable.Creator<Product>() {
            public Product createFromParcel(Parcel in) {
                return new Product(in);
            }
            
            public Product[] newArray(int size) {
                return new Product[size];
            }
 
	};
	
}
