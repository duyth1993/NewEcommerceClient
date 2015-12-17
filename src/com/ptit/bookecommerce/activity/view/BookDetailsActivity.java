package com.ptit.bookecommerce.activity.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paulusworld.drawernavigationtabs.MainActivity;
import com.paulusworld.drawernavigationtabs.R;
import com.ptit.bookecommerce.model.Book;
import com.ptit.bookecommerce.utils.Constants;
import com.ptit.bookecommerce.utils.Device;
import com.squareup.picasso.Picasso;

public class BookDetailsActivity extends Activity implements OnClickListener {
	TextView tvTitle, tvDiscount, tvIsbn, tvPrice, tvDescription, tvStock;
	Button btnAddToCart;
	ImageView ivCover;
	String value = null;
	Point p;
	Book b = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_details);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			value = extras.getString("book_id");
			Log.d("Value", value);
		}
		
		// get action bar   
        ActionBar actionBar = getActionBar();
 
        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Book Details");
		
		btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
		tvTitle = (TextView) findViewById(R.id.book_title);
		tvPrice = (TextView) findViewById(R.id.book_price);
		tvDiscount = (TextView) findViewById(R.id.book_discount);
		tvStock = (TextView) findViewById(R.id.book_stock);
		tvDescription = (TextView) findViewById(R.id.description);
		ivCover = (ImageView) findViewById(R.id.cover);
		tvIsbn = (TextView) findViewById(R.id.book_isbn);

		p = Device.getSizeImageDetails(this);
		RequestParams params = new RequestParams();
		params.put("book_id", value);
		getData(params, Constants.URL_BOOK);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getData(RequestParams params, String url) {
		// Make RESTful webservice call using AsyncHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO Auto-generated method stub
				Log.d("Out put", "Fail" + statusCode);
			}

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				try {
					// JSON Object
					JSONObject obj = new JSONObject(response);
					String url = Constants.URL_BOOK_IMAGE + obj.getString("cover_url").toString();
					int id = obj.getInt("id");
					String title = obj.getString("title").toString();
					String description = obj.getString("description")
							.toString();
					String isbn = obj.getString("isbn").toString();
					Float price = Float.parseFloat(obj.getString("price")
							.toString());
					int discount = obj.getInt("discount");
					int stock = obj.getInt("stock");
					int numPages = obj.getInt("num_pages");

					Picasso.with(getApplicationContext()).load(url)
							.resize(p.x, p.y).into(ivCover);
					tvTitle.setText(title);
					tvDescription.setText(description);
					tvIsbn.setText("ISBN: " + isbn);
					tvStock.setText("Stock: " + stock);
					tvPrice.setText("Price: $" + price);
					tvDiscount.setText("Discount: " + discount);

					b = new Book(id, isbn, title, description, url, price,
							discount, stock, numPages);
					btnAddToCart.setOnClickListener(BookDetailsActivity.this);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("BOOK DETAILS EXECPTION", e.getMessage());
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnAddToCart) {
			MainActivity.cart.addToCart(b);
			Toast.makeText(this, "Add to cart successfully", Toast.LENGTH_LONG).show();
		}
	}
}
