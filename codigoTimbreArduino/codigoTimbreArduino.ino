//Libreria LCD
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);

//Vectores que usaremos para el manejo de los horarios que se guardarán en el Arduino
String lista[] = {"", "", "", "", "", "", "", ""};
String listaHoras[] = {"","","","","","","","","","","","","","","","","","","","",""};
String datosHora[] = {"","","","","",""};
int horas[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
int minutos[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
int momentos[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
int timbres[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
int duracion[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
int dias[] = {0,0,0,0,0,0,0,0};
//Largo de la lista de horas y días que sonará
int lenHoras = 0;
int lenDias = 0;
//Datos que representan a la hora que se enviaron los datos para comenzar el reloj en el Arduino
int hora = -1;
int min = -1;
int seg = -1;
int dia = 0;
int toques = -1;
int segDuracion = 3;
int mom = -1;
bool estado = false;
int contSeg = -1;
int sig = 0;
int contList = 2;

void limpiarListas(){
  for(int i = 0; i < lenHoras; i++){
    listaHoras[i] = "";
    horas[i] = 0;
    minutos[i] = 0;
    momentos[i] = 0;
    timbres[i] = 0;
    duracion[i] = 0;
  }
  for(int i = 0; i < lenDias; i++){
    dias[i] = 0;
  }
}

void setup() {
  pinMode(8, OUTPUT);
  Serial.begin(9600);
  digitalWrite(8, HIGH);
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("EL TIMBRE NO");
  lcd.setCursor(0, 1);
  lcd.print("ESTA PROGRAMADO");
}
void split(String lst[], String cadena, String sep) {
  int inicio = 0;
  int fin = cadena.indexOf(sep, inicio);
  int i = 0;
  while (fin != -1) {
    lst[i] = cadena.substring(inicio, fin);
    inicio = fin+1;
    fin = cadena.indexOf(sep, inicio);
    i++;
  }
}
void splitNumbers(String cadena, String sep) {
  int inicio = 0;
  int fin = cadena.indexOf(sep, inicio);
  int i = 0;
  while (fin != -1) {
    dias[i] = cadena.substring(inicio, fin).toInt();
    inicio = fin+1;
    fin = cadena.indexOf(sep, inicio);
    i++;
  }
  lenDias = i;
}

void cargarDatos(){
  limpiarListas();
  hora = lista[1].toInt();
  min = lista[2].toInt();
  seg = lista[3].toInt();
  mom = lista[4].toInt();
  dia = lista[5].toInt();
  String cadenaHoras = lista[6].substring(0, lista[6].length());
  String cadenaDias = lista[7].substring(0, lista[7].length());
  split(listaHoras, cadenaHoras, "*");
  splitNumbers(cadenaDias, "*");
  int i = 0;
  while(!listaHoras[i].equals("")){
    split(datosHora, listaHoras[i], "-");
    horas[i] = datosHora[0].toInt();
    minutos[i] = datosHora[1].toInt();
    momentos[i] = datosHora[2].toInt();
    timbres[i] = datosHora[3].toInt();
    duracion[i] = datosHora[4].toInt();
    i++;
  }
  lenHoras = i;
}

bool comprobarHora(int h, int m, int moment, int d){
  for(int i = 0; i<lenHoras; i++){
      if(h==horas[i]&&m==minutos[i]&&moment==momentos[i]){
          for (int j = 0; j < lenDias; j++) {
              if(d==dias[j]){
                  toques = timbres[i];
                  segDuracion = duracion[i];
                  return true;
              }
          }
      }
  } return false;
}

void loop() {
  if (Serial.available()>0) {
    String msg = Serial.readString();
    String accion = "";
    if(msg.length()==1){
      accion = msg;
    } else {
      split(lista, msg, ",");
      accion = lista[0];
    }
    if (accion.equals("1")) {
      digitalWrite(8, LOW);
    } else if (accion.equals("0")) {
      digitalWrite(8, HIGH);
    } else if (accion.equals("2")){
      cargarDatos();
      lcd.clear();
      Serial.println("Timbre programado.");
    }
  }

  //Simulación de reloj
    if(hora>-1&&min>-1&&seg>-1){
      if(seg<59){
        seg++;
        if(contSeg<segDuracion&&toques>0&&estado){
            contSeg++;
            //if(contSeg==5) segDuracion = contSeg;
        }
        else if(estado&&contSeg==segDuracion){
            contSeg=-1;
            toques--;
        }
      }
      else {
        seg = 0;
        if(min<59){
          min++;
          if(comprobarHora(hora, min, mom, dia)) estado = true;
          else estado = false;
        }
        else {
          min = 0;
          if(comprobarHora(hora, min, mom, dia)) estado = true;
          else estado = false;
          if(hora<12) hora++;
          else {
            hora = 1;
            if(mom==0) mom = 1;
            else mom = 0;
          }
        }
      }
      if(contSeg==0){
        digitalWrite(8, LOW);
      } else if (contSeg==segDuracion||contSeg==5){
        digitalWrite(8, HIGH);
      }
      lcd.setCursor(0,0);
      lcd.print(agregarCero(hora));
      lcd.setCursor(2,0);
      lcd.print(":");
      lcd.setCursor(3,0);
      lcd.print(agregarCero(min));
      lcd.setCursor(5,0);
      lcd.print(":");
      lcd.setCursor(6,0);
      lcd.print(agregarCero(seg));
      lcd.setCursor(9,0);
      lcd.print(momento(mom));
      lcd.setCursor(12, 0);
      lcd.print(diaSem(dia));
      if(contList==2){
        if(horas[sig]>0){
          lcd.setCursor(0,1);
          lcd.print("Horas: ");
          lcd.setCursor(7,1);
          lcd.print(agregarCero(horas[sig]));
          lcd.setCursor(9,1);
          lcd.print(":");
          lcd.setCursor(10,1);
          lcd.print(agregarCero(minutos[sig]));
          lcd.setCursor(12,1);
          lcd.print(momento(momentos[sig]));
          sig++;
          contList = 0;
        } else sig = 0;
      } else contList++;
      delay(990);
    }
    
}

String momento(int m){
  if(m==0) return "AM";
  else return "PM";
}

String diaSem(int d){
  String salida = "";
  switch(d){
    case 1: salida = "Dom"; break; 
    case 2: salida = "Lun"; break; 
    case 3: salida = "Mar"; break; 
    case 4: salida = "Mie"; break; 
    case 5: salida = "Jue"; break; 
    case 6: salida = "Vie"; break; 
    case 7: salida = "Sab"; break; 
  }
  return salida;
}

String agregarCero(int n){
  String salida = "";
  if(n<10) {
    salida = "0";
  }
  salida.concat(n);
  return salida;
}