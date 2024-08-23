package com.example.site_supervisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class DeliveryChallanAdapter extends ArrayAdapter
{
    Context cont;
    int resource;
    List<DeliveryChallanPojo> challan;
    SQLiteDatabase db = null;
    public String dbPath = "/data/data/com.example.site_supervisor/databases/";
    public static String dbName= "Site_Supervisor.db";
    String path = dbPath+dbName;

    public DeliveryChallanAdapter(@NonNull Context cont, int resource, @NonNull List<DeliveryChallanPojo> challan)
    {
        super(cont, resource, challan);
        this.cont = cont;
        this.resource = resource;
        this.challan = challan;
    }

    public View getView(final int position, View convetView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(cont);
        View view = inflater.inflate(resource,null,false);

        ImageView imgEdit = view.findViewById(R.id.imgEdit);

        EditText et[] = new EditText[5];
        int id[] = {R.id.etPosition,R.id.etCode,R.id.etItemName,R.id.etUnit,R.id.etQty};

        for(int i=0; i<id.length; i++)
        {
            et[i] = view.findViewById(id[i]);
        }

        et[0].setText(""+(position+1));
        et[1].setText(challan.get(position).getCode());
        et[2].setText(challan.get(position).getItemName());
        et[3].setText(challan.get(position).getUnit());
        et[4].setText(""+challan.get(position).getQty());

        if(challan.get(position).getEditable())
        {
            for(int i=1; i<et.length; i++)
            {
                et[i].setEnabled(true);
                et[i].setFocusable(true);
                et[i].setFocusableInTouchMode(true);
                et[i].setClickable(true);
            }
            imgEdit.setImageResource(R.drawable.done);
        }
        else
        {
            for(int i=1; i<et.length; i++)
            {
                et[i].setEnabled(false);
                et[i].setFocusable(false);
                et[i].setFocusableInTouchMode(false);
                et[i].setClickable(false);
            }

            imgEdit.setImageResource(R.drawable.edit);
        }

        imgEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(challan.get(position).getEditable())
                {
                    try
                    {
                        db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(cont,"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    try
                    {
                        challan.get(position).setEditable(false);
                        if(et[1].getText().toString().equals(""))
                        {
                            throw new EmptyStringException();
                        }
                        challan.get(position).setCode(et[1].getText().toString().toUpperCase());
                        challan.get(position).setItemName(et[2].getText().toString());
                        challan.get(position).setUnit(et[3].getText().toString());
                        challan.get(position).setQty(Float.parseFloat(et[4].getText().toString()));

                        String updateQuery = "update tbl_dc_details set itemcode = '"+challan.get(position).getCode()+"', ";
                        updateQuery += "itemname = '"+challan.get(position).getItemName()+"', ";
                        updateQuery += "uom = '"+challan.get(position).getUnit()+"', ";
                        updateQuery += "qty = '"+challan.get(position).getQty()+"' ";
                        updateQuery += "where id = "+challan.get(position).getId();

                        try
                        {
                            db.execSQL(updateQuery);
                            db.close();
                            Toast.makeText(cont,"Record Updated",Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(cont,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        for(int i=1; i<et.length; i++)
                        {
                            et[i].setEnabled(false);
                            et[i].setFocusable(false);
                            et[i].setFocusableInTouchMode(false);
                            et[i].setClickable(false);
                        }
                        imgEdit.setImageResource(R.drawable.edit);
                    }
                    catch (NumberFormatException nfe)
                    {
                        Toast.makeText(cont,"Qty should be Numeric...!",Toast.LENGTH_SHORT).show();
                    }
                    catch (EmptyStringException ese)
                    {
                        Toast.makeText(cont,ese.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    for(int i=1; i<et.length; i++)
                    {
                        et[i].setEnabled(true);
                        et[i].setFocusable(true);
                        et[i].setFocusableInTouchMode(true);
                        et[i].setClickable(true);
                    }

                    challan.get(position).setEditable(true);
                    imgEdit.setImageResource(R.drawable.done);
                }
            }
        });
        return view;
    }

//    private void showPopupMenu(View view)
//    {
//        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_bolt, null);
//
//        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        boolean focusable = true;
//
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//        EditText etBolt = popupView.findViewById(R.id.etBolt);
//        EditText etQty = popupView.findViewById(R.id.etQty);
//        Button btAdd = popupView.findViewById(R.id.btAdd);
//
//        btAdd.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                try
//                {
//                    db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
//                }
//                catch (Exception e)
//                {
//                    Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//                Cursor cur = null;
//
//                try
//                {
//                    cur = db.rawQuery("select id from tbl_boltlist where upper(type) like '%"+etBolt.getText().toString().toUpperCase()+"%'",null);
//                    cur.moveToFirst();
//                    int id = cur.getInt(0);
//
//                    cur = db.rawQuery("select Max(id) from tbl_boltlist",null);
//                    cur.moveToFirst();
//                    id = cur.getInt(0)+1;
//
//                    BoltListPojo blp = new BoltListPojo();
//                    blp.setId(id);
//                    if(etBolt.getText().toString().equals(""))
//                    {
//                        throw new EmptyStringException();
//                    }
//                    blp.setType(etBolt.getText().toString().toUpperCase());
//                    blp.setQty(Integer.parseInt(etQty.getText().toString()));
//
//                    ContentValues values = new ContentValues();
//                    values.put("id", id);
//                    values.put("ProjectID", getIntent().getIntExtra("projectId",0));
//                    values.put("type",blp.getType());
//                    values.put("qty",blp.getQty());
//                    values.put("date",getIntent().getStringExtra("date"));
//
//                    long newRowId = db.insert("tbl_boltlist", null, values);
//
//                    if (newRowId == -1)
//                    {
//                        Toast.makeText(getApplicationContext(), "Error inserting data", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        Toast.makeText(getApplicationContext(), "Data inserted with row ID: " + newRowId, Toast.LENGTH_SHORT).show();
//                    }
//
//                    cur.close();
//                    db.close();
//                    bolt.add(blp);
//                    popupWindow.dismiss();
//                }
//                catch (NumberFormatException nfe)
//                {
//                    cur.close();
//                    db.close();
//                    Toast.makeText(getApplicationContext(),"Qty should be Integer...!",Toast.LENGTH_SHORT).show();
//                }
//                catch (EmptyStringException ese)
//                {
//                    cur.close();
//                    db.close();
//                    Toast.makeText(getApplicationContext(),ese.toString(),Toast.LENGTH_SHORT).show();
//                }
//                catch (Exception e)
//                {
//                    cur.close();
//                    db.close();
//                    Toast.makeText(getApplicationContext(),"Bolt type doesn't exist...!",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
//    }

}
