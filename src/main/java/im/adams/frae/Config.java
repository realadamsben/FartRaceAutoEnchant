package im.adams.frae;

public class Config {

    int xPosEnchant;
    int yPosEnchant;
    int xPosConfirm;
    int yPosConfirm;
    int xPosTopLeft;
    int yPosTopLeft;
    int xPosBottomRight;
    int yPosBottomRight;
    int targetNum;
    String webhookURI;

    public Config(){
        xPosEnchant = 0;
        yPosEnchant = 0;
        xPosConfirm = 0;
        yPosConfirm = 0;
        xPosTopLeft = 0;
        yPosTopLeft = 0;
        xPosBottomRight = 0;
        yPosBottomRight = 0;
        targetNum = 2;
        webhookURI = "";
    }
}
