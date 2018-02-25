#include <SPI.h>
#include <boards.h>
#include <RBL_nRF8001.h>

unsigned char *bufferData[1]; //Buffer set up
//Hardware pin definitions
int uvoutML = A1; //Output from the ML sensor
int ref_3V3ML = A5; //3.3V power on the Arduino board for ML sensor

void setup() {
  Serial.begin(9600);
  ble_set_name("GG");
  // start BLE
  ble_begin();
  //Set up pinmodes
  pinMode(uvoutML, INPUT);
  pinMode(ref_3V3ML, INPUT);
}

void loop() {
  //GUVA calculations
  float sensorVoltage;
  float sensorValue;
  sensorValue = averageAnalogRead(A0);
  sensorVoltage = sensorValue / 1024 * 3.3;
  int uvIndex = UVindexGUVA(sensorVoltage * 10);
  //ML calculations
  int uvLevel = averageAnalogRead(uvoutML);
  int refLevel = averageAnalogRead(ref_3V3ML);
  float outputVoltage = 3.3 / refLevel * uvLevel;
  float uvIntensity = mapfloat(outputVoltage, 0.99, 2.8, 0.0, 15.0); //Convert the voltage to a UV intensity level
  //uvINtensity and UVindex is the same since our sampling rate is only 1 second
  float avgUV = ((uvIntensity + uvIndex) / 2);
  //Convert for ble write
  char *bufferUV = UVvalue(avgUV);
  char *buffer1 =  int2uCharArray(bufferUV);
  bufferData[0] = buffer1;
  //Write to ble only if ble is connected
  if ( ble_connected() )
  {
    // write test data to BLE write buffer
    ble_write_bytes(bufferData[0], sizeof(bufferData[0]) );
  }
  // use built-in function to transmit data
  ble_do_events();
  delay(1000);
}


unsigned char* int2uCharArray(int value)
{
  //conver int to char via array buffer
  char buffer[1] = {0};
  unsigned char ubuffer[1] = {0};
  itoa(value, buffer, 10);
  for (int i = 0; i < 1; i++)
    ubuffer[i] = buffer[i] - 48; //Fixed values

  for (int i = 0; i < 1; i++)
    Serial.println(ubuffer[i]);

  return ubuffer;
}

float UVindexGUVA(float sensorVoltage) {
  //Using UV index chart, return values of UV Index based on Voltage
  if (sensorVoltage >= 1.17 )
  {
    return 11.00;
  }
  else if (sensorVoltage >= 1.079 )
  {
    return 10.00;
  }
  else if (sensorVoltage >= .976 )
  {
    return 9.00;
  }
  else if (sensorVoltage >= .881 )
  {
    return 8.00;
  }
  else if (sensorVoltage >= .795 )
  {
    return 7.00;
  }
  else if (sensorVoltage >= .696 )
  {
    return 6.00;
  }
  else if (sensorVoltage >= .606 )
  {
    return 5.00;
  }
  else if (sensorVoltage >= .503 )
  {
    return 4.00;
  }
  else if (sensorVoltage >= .408 )
  {
    return 3.00;
  }
  else if (sensorVoltage >= .318 )
  {
    return 2.00;
  }
  else if (sensorVoltage >= .227 )
  {
    return 1.00;
  }
  else
  {
    return 0.00;
  }
}

int averageAnalogRead(int pinToRead)
{
  //Give avg of 8 reading for more accurate data
  byte numberOfReadings = 8;
  unsigned int runningValue = 0;

  for (int x = 0 ; x < numberOfReadings ; x++)
    runningValue += analogRead(pinToRead);
  runningValue /= numberOfReadings;

  return (runningValue);
}

//The Arduino Map function but for floats
//From: http://forum.arduino.cc/index.php?topic=3922.0
float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

char *UVvalue(float avgUV)
{
  //returns as a char *
  if (avgUV >= 11.01)
  {
    return 11;
  }
  else if (avgUV >= 10.01)
  {
    return 10;
  }
  else if (avgUV >= 9.01)
  {
    return 9;
  }
  else if (avgUV >= 8.01)
  {
    return 8;
  }
  else if (avgUV >= 7.01)
  {
    return 7;
  }
  else if (avgUV >= 6.01)
  {
    return 6;
  }
  else if (avgUV >= 5.01)
  {
    return 5;
  }
  else if (avgUV >= 4.01)
  {
    return 4;
  }
  else if (avgUV >= 3.01)
  {
    return 3;
  }
  else if (avgUV >= 2.01)
  {
    return 2;
  }
  else if (avgUV >= 1.01)
  {
    return 1;
  }
  else
  {
    return 0;
  }
}
