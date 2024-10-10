package br.com.DnotasTecnologia.Dnotas.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import br.com.DnotasTecnologia.Dnotas.R;


import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> produtosList;
    private final List<Double> valoresList;
    private final TextView valorTotal;
    private final double[] totalValue;

    public CustomAdapter(Context context, List<String> produtosList, List<Double> valoresList, TextView valorTotal, double[] totalValue) {
        super(context, 0, produtosList);
        this.context = context;
        this.produtosList = produtosList;
        this.valoresList = valoresList;
        this.valorTotal = valorTotal;
        this.totalValue = totalValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
    
        String produto = produtosList.get(position);
        Double valor = valoresList.get(position);
    
        TextView produtoTextView = convertView.findViewById(R.id.itemName);
        TextView valorTextView = convertView.findViewById(R.id.itemValue);
        Button removeButton = convertView.findViewById(R.id.removeButton);
    
        produtoTextView.setText(produto); // Exibe o nome do produto
        valorTextView.setText(String.format("R$ %.2f", valor)); // Exibe o valor do produto
    
        removeButton.setOnClickListener(v -> {
            // Remove o item da lista
            produtosList.remove(position);
            valoresList.remove(position);
    
            // Atualiza o totalValue
            totalValue[0] -= valor;
            valorTotal.setText(String.format("R$ %.2f", totalValue[0]));
    
            // Notifica o adaptador sobre a mudança
            notifyDataSetChanged();
        });
    
        return convertView;
    }
}
