package m1.nayak.m1.objects;

import java.util.Date;

/**
 * Created by Ashwin on 12/18/14.
 */
public class FlashCard extends Question {

    public boolean trueFalse;

    public FlashCard(String e, String i, String q, String a, double s, Date last, String sub, String top) {
        super(e, i, q, a, s, last, sub, top);
        trueFalse = false;
    }

}
