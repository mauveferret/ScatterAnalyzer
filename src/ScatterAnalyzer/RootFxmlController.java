package ScatterAnalyzer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RootFxmlController {

    final FileChooser fileChooser = new FileChooser();
    private boolean accessGranted=true;


    //Buttons for calculating
    @FXML
    private CheckBox getTXT;
    @FXML
    private CheckBox NER;
    @FXML
    private CheckBox NEY;
    @FXML
    private CheckBox NThetaR;
    @FXML
    private CheckBox NThetaY;
    @FXML
    private CheckBox NThetaPhiR;
    @FXML
    private CheckBox NThetaPhiY;
    @FXML
    private TextField StringCount;

    ///////////////////////////

    @FXML
    private TextField time;
    @FXML
    private Label secret;
    @FXML
    private Button button;
    //Energy spectrum buttons
    @FXML
    private TextField E0;
    @FXML
    private TextField energyResolution;
    @FXML
    private TextField polarAngleNE;
    @FXML
    private TextField dPolarAngleNE;
    @FXML
    private TextField azimuthAngleNE;
    @FXML
    private TextField dAzimuthAngleNE;
    //PolarAngle spectrum buttons

    //energy?

    @FXML
    private TextField dPolarAngleNtheta;
    @FXML
    private  TextField azimuthAngleNtheta;
    @FXML
    private TextField dAzimuthAngleNtheta;

    //Surface particle distribution
    @FXML
    private TextField NdThetaPhi;
    @FXML
    private TextField NThetadPhi;

    // particles coefficients fields
    @FXML
    private TextField count;
    @FXML
    private TextField scattered;
    @FXML
    private TextField sputtered;
    @FXML
    private  TextField projected;
    @FXML
    private TextField numberOfParticlesInScatter;
    @FXML TextField energyScattering;
    //TRIM
    @FXML
    private Slider FileType;


    String path="lol";

    @FXML
    public void readme()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new Main().showHelpPage("readme.png");
            }
        });
    }
    @FXML
    public void pushed() {



       if (path.equals("lol"))

       {
           //looking for SC*.dat in  jar directory
           String myJarPath = RootFxmlController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
           String dirPath = new File(myJarPath).getParent();
           FileChooser.ExtensionFilter ScatterFilter =
                   new FileChooser.ExtensionFilter("SCATTER/TRIM (.dat, .txt)", "*.dat","*.txt");
           fileChooser.setTitle("Необходимо выбрать файл SC******.dat (или *.txt в случае TRIM)");
           fileChooser.getExtensionFilters().add(ScatterFilter);
           fileChooser.setSelectedExtensionFilter(ScatterFilter);
           fileChooser.setInitialDirectory(new File(dirPath));
       }
       else
           //look file in previous directory
           fileChooser.setInitialDirectory(new File(path.substring(0, path.lastIndexOf("\\"))));
       File  file = fileChooser.showOpenDialog(button.getScene().getWindow());
       path = file.getAbsolutePath();

        if (file.getPath().contains("SC")) FileType.setValue(0);
        else
       if (file.getPath().contains(".txt"))
       {
           try {

               FileType.setValue(100);
               BufferedReader br = new BufferedReader(new FileReader(path));
               //rubbish lines
               String line = br.readLine();
               while (!line.contains("TRIM Calc.")) line = br.readLine();
               //find out the initial energy
               int energy = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf("keV") - 1)) * 1000;
               E0.setText(""+energy);
               energyResolution.setText(""+energy/100);
               br.close();
           }
           catch (Exception e)
           {

           }
       }

}

    @FXML
    public void AngleChanged()
    {
        try {
            double t = Double.parseDouble(polarAngleNE.getText());
            if (t < 0 || t > 90) {
                polarAngleNE.setText("71");

                new Main().showNotification("Установите угол  в пределах 0<=t<=90");
            }
        }
        catch (Exception e)
        {
            polarAngleNE.setText("71");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNE.getText());
            if (t < 0 || t > 180) {
                azimuthAngleNE.setText("0");

                new Main().showNotification("Установите угол  в пределах 0<=t<=180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNE.setText("0");
        }
        try {
            double t = Double.parseDouble(azimuthAngleNtheta.getText());
            if (t < 0 || t > 179 ) {
                azimuthAngleNtheta.setText("0");

                new Main().showNotification("Установите угол  в пределах 0<=t<180");
            }
        }
        catch (Exception e)
        {
            azimuthAngleNtheta.setText("0");
        }
    }
    @FXML
    public void DeltaChanged()
    {
        try {
            double t = Double.parseDouble(dPolarAngleNE.getText());
            if (t <= 0 ) {
                dPolarAngleNE.setText("5");

                new Main().showNotification("Установите разброс по углу больше нуля!");

            }
        }
        catch (Exception e)
        {
            dPolarAngleNE.setText("5");
        }
        try {
            double t = Double.parseDouble(dAzimuthAngleNE.getText());
            if (t <= 0 ) {
                dAzimuthAngleNE.setText("5");

                new Main().showNotification("Установите разброс по углу больше нуля!");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNE.setText("5");
        }
        try {
            double t = Double.parseDouble(dAzimuthAngleNtheta.getText());
            if (t <= 0 ) {
                dAzimuthAngleNtheta.setText("5");

                new Main().showNotification("Установите разброс по углу больше нуля!");

            }
        }
        catch (Exception e)
        {
            dAzimuthAngleNtheta.setText("5");
        }
    }

    @FXML
    public void EChanged()
    {
        try {
            double E = Double.parseDouble(E0.getText());
            if (E<0) throw new Exception();
            energyResolution.setText((int) (E/100)+"");
            if (E==100000)
            {
                new Main().Futurama();
                new Main().showNotification("ничто не вечно под Луной ... кроме SCATTER");
            }
        }
        catch (Exception e)
        {
            E0.setText("9000");
        }

    }

    @FXML
    public void YouDoWhatYouGonnaDo()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new Main().Futurama();
            }
        });
    }
    @FXML
    public void ResChanged()
    {
        try {
            double E = Double.parseDouble(energyResolution.getText());
            if (E<0) throw new Exception();
        }
        catch (Exception e)
        {
            energyResolution.setText("90");
        }
    }

    @FXML
    public void secretLaunch()
    {
        if (secret.getText().equals("А не попить ли чаю?")) secret.setText("");
        else
        secret.setText("А не попить ли чаю?");

    }

    @FXML
    public  void showHelp()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new Main().showHelpPage("axes.png");
            }
        });

    }

    @FXML
    public  void runCalculation()
    {

        if (path.equals("lol"))
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Main().showNotificationAboutFile();
                }
            });
        }

        else  if (accessGranted)
        {


            new Thread(new Runnable() {

                @Override
                public void run() {
                    int E;
                    if (!E0.getText().equals("Ждите"))
                         E=Integer.parseInt(E0.getText());
                    else E=9000;

                    int energyReturnValue = E;
                    int dE = Integer.parseInt(energyResolution.getText());
                    double thetaNE = Double.parseDouble(polarAngleNE.getText());
                    double dThetaNE = Double.parseDouble(dPolarAngleNE.getText());
                    double phiNE=Double.parseDouble(azimuthAngleNE.getText());
                    double dphiNE=Double.parseDouble(dAzimuthAngleNE.getText());

                    ///////////

                    double dThetaNTheta=Double.parseDouble(dPolarAngleNtheta.getText());
                    double phiNTheta=Double.parseDouble(azimuthAngleNtheta.getText());
                    double dPhiNTheta=Double.parseDouble(dAzimuthAngleNtheta.getText());
                    double NThetadPhi1 = Double.parseDouble(NThetadPhi.getText());
                    double NdThetaPhi1 = Double.parseDouble(NdThetaPhi.getText());

                    E0.setText("Ждите");

                    boolean IsScatter = (FileType.getValue() <50);

                   double[] data = new Analyzer().calculateSpectra(path, E, dE , thetaNE ,dThetaNE, phiNE,dphiNE, 1, dThetaNTheta,
                           phiNTheta, dPhiNTheta, NER.isSelected(), NEY.isSelected(), NThetaR.isSelected(),
                           NThetaY.isSelected(), Integer.parseInt(StringCount.getText()), NThetaPhiR.isSelected(),NThetaPhiY.isSelected(), NThetadPhi1, NdThetaPhi1, getTXT.isSelected(), IsScatter);
                   //path="lol";
                    E0.setText(E+"");
                    double initialCount=0;
                    if (Double.parseDouble(numberOfParticlesInScatter.getText())<15) initialCount=Math.pow(10,  (int) (Math.ceil(Math.log10(data[1] + 0.5))) );
                    else initialCount=Double.parseDouble(numberOfParticlesInScatter.getText());
                    time.setText(""+data[0]);
                    count.setText(data[1]+"");
                    if ((Double.parseDouble(numberOfParticlesInScatter.getText())<15)&(!IsScatter)) initialCount=data[1];
                    scattered.setText(new BigDecimal(data[2]/initialCount).setScale(5, RoundingMode.UP).doubleValue()+"");
                    sputtered.setText(new BigDecimal(data[3]/initialCount).setScale(5, RoundingMode.UP).doubleValue()+"");
                    projected.setText(new BigDecimal(data[4]/initialCount).setScale(5, RoundingMode.UP).doubleValue()+"");
                    E0.setText(""+energyReturnValue);
                    energyScattering.setText(new BigDecimal(data[5]/(initialCount*E)).setScale(5, RoundingMode.UP).doubleValue()+"");
                }
            }).start();
        }
    }
}
