import javax.swing.*;
import java.awt.*;
public class PlayerStats extends PlayerStatMenu{

    private int numHousesBuilt = 0;
	public PlayerStats(int i){
		super(i);
	}
	
	public PlayerStats clone(){
		return this;
	}

	public void setMoneyLabel(int money) {

        String moneyString = String.valueOf(money);
        moneyLabel.setText(moneyString);
    }

    public void setPropertyLabel(int size) {
        String numProperties = String.valueOf(size);
        propertyLabel.setText(numProperties);
    }
    public JLabel getMoneyLabel() {
        return super.moneyLabel;
    }

    public JLabel getPropertyLabel() {
        return super.propertyLabel;
    }


    // Add getter and setter for numHousesBuilt
    public int getNumHousesBuilt() {
        return numHousesBuilt;
    }

    public void setNumHousesBuilt(int numHousesBuilt) {
        this.numHousesBuilt = numHousesBuilt;
    }
}

