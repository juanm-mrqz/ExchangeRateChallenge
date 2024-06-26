package com.alura.app.main;

import com.alura.app.main.utility.CurrencyCode;
import com.alura.app.main.utility.LocalDateTimeTypeAdapter;
import com.alura.app.main.utility.UserData;
import com.alura.app.currency.Conversion;
import com.alura.app.currency.CurrencyData;
import com.alura.app.currency.QuotaData;
import com.alura.app.servicios.ApiAccess;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppManager {

    private ApiAccess apiAccess;
    private static final String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private Properties properties = new Properties();
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private UserData userData;



    public AppManager(ApiAccess apiAccess, String configFile) {

        this.apiAccess = apiAccess;
        try{
            this.properties.load(new FileInputStream(rootPath+configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Archivo de configuracion no encontrado");
        } catch (IOException e) {
            throw new RuntimeException("Error de I/O");
        }
    }

    //Endpoints de la api en config.properties
    private String getLatestPath() {
        return BASE_URL + properties.getProperty("api_key") + properties.getProperty("latest");
    }
    private String getQuotaPath() {
        return BASE_URL + properties.getProperty("api_key") + properties.getProperty("quota");
    }

    private String historialFilePath() {
        return rootPath + properties.getProperty("historial");
    }


    String mainMenu = """
            1. Realizar una conversion de moneda
            2. Mostrar otras monedas disponibles
            3. Consultar saldo disponible a la api
            4. Mostrar historial de consultas
            5. Eliminar historial de consultas
            0. Salir
            """;

    public void showMenu() {
        System.out.println("Bienvenido al sistema conversor de monedas");
        int option = -1;
        while(option != 0)
        {
            System.out.println(mainMenu);
            option = new Scanner(System.in).nextInt();
            switch(option) {
                case 1:
                    saveAndShow(convertCurrency());
                    break;
                case 2:
                    showAvailableCodes();
                    break;
                case 3:
                    showQuota();
                    break;
                case 4:
                    showHistory();
                    break;
                case 5:
                    cleanHistory();
                    break;
                case 0:
                    option = 0;
                    break;
                default:
                    break;
            }

        }
    }

    /*
    Pedir datos al usuario para poder realizar la busqueda y
    pasar luego los datos al Conversion object
    */
    private void getUserData(){

        System.out.println("Ingrese el codigo de la moneda de origen");
        String baseCode =  new Scanner(System.in).nextLine().toUpperCase();
        System.out.println("Ingrese el codigo de la moneda de destino");
        String targetCode =  new Scanner(System.in).nextLine().toUpperCase();
        System.out.println("Ingrese el valor que desea convertir");
        Double value =  new Scanner(System.in).nextDouble();

        this.userData = new UserData(baseCode, targetCode, value);
    }

    //Con los datos del usuario, realiza la llamada a la api y devuelve un objeto
    //con dichos datos
    private CurrencyData getCurrencyDataFromApi() {
        getUserData();
        //Hacer la llamada a la Api con el codigo base
        return apiAccess.getCurrencyResponse(
                getLatestPath()+
                        userData.baseCode());
    }

    //Realizar la logica de la conversion
    public Conversion convertCurrency() {

        //Hacer la llamada a la Api con el codigo base
        CurrencyData response = getCurrencyDataFromApi();

        //Realizar los calculos y guardar en un objeto Conversion
        Conversion conv = new Conversion(userData);
        conv.setBaseCurrency(
                response.conversion_rates().get(userData.baseCode()).getAsDouble()
        );
        conv.setTargetCurrency(
                response.conversion_rates().get(userData.targetCode()).getAsDouble()
        );

        //Setear resultado de la conversion en el objeto
        conv.calculate();
        return conv;

    }

    //Guardar datos en el historial y mostrar los resultados
    private void saveAndShow(Conversion conversion) {
        //1. Guardar el objeto en el historial
        addToHistory(conversion);
        //2.Mostrar resultados al usuario
        showConversionInfo(conversion);
    }

    //2. Mostrar los codigos disponibles para consultar
    private void showAvailableCodes(){
        CurrencyCode.getAllCurrencies().forEach
                ((k, v) -> System.out.println(k + " - " + v));
    }



    //3. Consultar el historial
    private List<Conversion> getHistory(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setPrettyPrinting().create();

        //Read json records
        try(FileReader reader = new FileReader(historialFilePath())){
            List<Conversion> currentHistory =
                    gson
                    .fromJson(reader, new TypeToken<ArrayList<Conversion>>() {}.getType());

            return currentHistory;

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void addToHistory(Conversion conversionData){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setPrettyPrinting().create();

        //Consultar si ya existe el archivo de historial
        List<Conversion> currentHistory= getHistory();
        //Write record to file
        try (FileWriter fileWriter = new FileWriter(historialFilePath())){
            if (currentHistory != null) {
                currentHistory.add(conversionData);
                gson.toJson(currentHistory, fileWriter);
            }
            else {
                gson.toJson(Arrays.asList(conversionData), fileWriter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //4. Eliminar el historial
    private void cleanHistory() {
        try{
            File fileToDelete = new File(historialFilePath());
            if (fileToDelete.delete()){
                System.out.println("\nHistorial Borrado\n");
            }
            else {
                System.out.println("El historial no pudo ser eliminado o no existe aun");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }





    //Formatos de salida en consola
    private void showHistory(){
        System.out.println("===== Historial de consultas ======\n");
        List<Conversion> historial = getHistory();
        if(historial != null)
            historial
                .forEach(this::showConversionInfo);
        else
            System.out.println("El historial se encuentra vacio");
        System.out.println("======================================\n");
    }

    private void showQuota() {
        QuotaData quota = apiAccess.getQuotaResponse(getQuotaPath());
        System.out.println("\n============Datos de solicitudes====================");
        System.out.println("Solicitudes totales por mes: " + quota.plan_quota());
        System.out.println("Solicitudes disponibles " + quota.requests_remaining());
        System.out.println("Dia de renovacion mensual: " + quota.refresh_day_of_month());
        System.out.println("====================================================\n");
    }
    private void showConversionInfo(Conversion conversion) {
        System.out.println("\n====================================");
        System.out.println("Fecha: " +conversion.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Moneda origen = " + conversion.getBaseCode());
        System.out.println("Moneda destino = " + conversion.getTargetCode());
        System.out.printf("Resultado : %s %s = %s %s%n",
                conversion.getValue(), conversion.getBaseCode(),
                conversion.getResult(), conversion.getTargetCode());
        System.out.println("====================================\n");

    }





}
