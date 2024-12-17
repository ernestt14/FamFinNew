package com.example.finnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Expense> expenseList;
    private LayoutInflater inflater;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expense_item, parent, false);
        }

        TextView tvExpenseDetails = convertView.findViewById(R.id.tvExpenseDetails);

        // Get the Expense object at the current position
        Expense expense = expenseList.get(position);

        // Format the expense details to include the location
        String expenseDetails = "Amount: " + expense.amount + "\nCategory: " + expense.category + "\nDate: " + expense.date
                + "\nLocation: Latitude: " + expense.latitude + ", Longitude: " + expense.longitude;
        tvExpenseDetails.setText(expenseDetails);

        return convertView;
    }
}
