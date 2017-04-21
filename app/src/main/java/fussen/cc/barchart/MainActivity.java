package fussen.cc.barchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Float> verticalList;
    private List<String> horizontalList;
    private BarChart barChart;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button more = (Button) findViewById(R.id.more);
        Button small = (Button) findViewById(R.id.small);

        barChart = (BarChart) findViewById(R.id.barchart);

        more.setOnClickListener(this);
        small.setOnClickListener(this);
        verticalList = new ArrayList<>();
        horizontalList = new ArrayList<>();


        for (int i = 0; i < 6; i++) {
            horizontalList.add("" + i);
        }

        random = new Random();
        while (verticalList.size() < 6) {
            int randomInt = random.nextInt(1000);
            verticalList.add((float) randomInt);
        }

        barChart.setHorizontalList(horizontalList);
        barChart.setVerticalList(verticalList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more:
                horizontalList.clear();
                verticalList.clear();
                for (int i = 0; i < 30; i++) {
                    horizontalList.add("" + i);
                }

                while (verticalList.size() < 30) {
                    int randomInt = random.nextInt(1000);
                    verticalList.add((float) randomInt);
                }

                barChart.setHorizontalList(horizontalList);
                barChart.setVerticalList(verticalList);

                break;
            case R.id.small:

                horizontalList.clear();
                verticalList.clear();
                for (int i = 0; i < 6; i++) {
                    horizontalList.add("" + i);
                }

                while (verticalList.size() < 6) {
                    int randomInt = random.nextInt(1000);
                    verticalList.add((float) randomInt);
                }

                barChart.setHorizontalList(horizontalList);
                barChart.setVerticalList(verticalList);
                break;
        }
    }
}
