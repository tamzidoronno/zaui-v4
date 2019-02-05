/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopstripe;

import com.stripe.Stripe;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author boggi
 */
public class GetShopStripe {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        
        // TODO code application logic here
        String key = "sk_live_iWVLQ1EuLLYo0U1NJE7nsNgn";
        String card = "4299412252922611";
        Integer expMonth = 06;
        Integer expYear = 21;
        
        GetShopStripe striper = new GetShopStripe();
//        UserCard savedCard = striper.saveCard(card, expMonth, expYear, key, "pal@getshop.com");
        
        
    }
    
    public UserCard saveCard(String card, Integer expMonth, Integer expYear, String key, String email) throws Exception {
        Stripe.apiKey = key;

        // Create a Customer:
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("email", email);
        //Somehow put this into the source and create the customer.
        Customer customer = Customer.create(chargeParams);

        Map<String, Object> cardParams = new HashMap<String, Object>();
        cardParams.put("object", "card");
        cardParams.put("number", card);
        cardParams.put("exp_month", expMonth);
        cardParams.put("exp_year", expYear);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("source", cardParams);
        Card savedCard = (Card)customer.getSources().create(params);

        UserCard ucard = new UserCard();
        Card stripecard = savedCard;
        ucard.expireMonth = stripecard.getExpMonth();
        ucard.expireYear = stripecard.getExpYear();
        ucard.card = customer.getId();
        ucard.mask = stripecard.getLast4();
        ucard.savedByVendor = "stripe";
        ucard.id = stripecard.getId();
        ucard.registeredDate = null;
        return ucard;
    }    

}
