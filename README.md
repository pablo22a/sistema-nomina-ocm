## Software necesario
- **Java JDK 17 o superior**
- **Microsoft Visual C++ Redistributable** (necesario para instalar MySQL Server)
- **MySQL Server 8.0 o superior**
- **MySQL Workbench** (opcional pero recomendado para gestionar la base de datos)

#### Sistema operativo probado
- Windows 11

## Instalación paso a paso

### 1. Instalar el JDK de Java

#### Descarga oficial de Oracle
1. Ir a [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/)
2. Dar clic en la pestaña Java Archive y buscar Java SE 17
3. En la versión más reciente, buscar la que diga Windows x64 Installer
4. Dar clic en el enlace para comenzar la descarga
5. Ejecutar el instalador y seguir las instrucciones
6. Verificar la instalación:
	- Abrir símbolo del sistema
	- Ejecutar: `java -version`

### 2. Instalar Redistributable de Microsoft
1. Ir a [Latest supported Visual C++ Redistributable downloads | Microsoft Learn](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170#latest-supported-redistributable-version)
2. Navegar hasta la sección **Latest supported redistributable version**
3. Dar clic en la versión X64
4. Ejecutar el instalador y seguir las instrucciones

### 3. Instalar MySQL Server
1. Ir a [MySQL Community Downloads](https://dev.mysql.com/downloads/mysql/)
2. Elegir la versión 8.4.7 LTS
3. Elegir el Sistema Operativo Microsoft Windows
4. Descargar el instalador **Windows (x86, 64-bit), MSI Installer**
5. Ejecutar el instalador y seguir las instrucciones
6. Dejar marcado **Open MySQL Configurator**

#### Pasos para configurar MySQL
1. En **Data Directory** escoge una ruta para MySQL
2. En **Type and Networking** escoge *Development Computer*. Para *Connectivity*, deja los valores por defecto.
3. En **Account and Roles** ingresa la contraseña: **root**
4. Da clic en *next* hasta llegar a **Apply Configuration**. Haz clic en *execute* y después en *next*
5. Si todo salió correctamente, da clic en *finish* para terminar la instalación

#### Agregar MySQL al Path
1. Busca la carpeta **MySQL** en la ruta que elegiste durante la instalación  
2. Navega hasta la carpeta **bin**  
3. Da clic derecho en la carpeta y selecciona **Copiar como ruta de acceso**  
4. En Windows, busca *Editar las variables de entorno del sistema*  
5. Haz clic en **Variables de entorno**  
6. En **Variables del sistema**, busca la variable **Path** y da clic en **Editar**  
7. Haz clic en **Nuevo** y pega la ruta copiada en el paso 3  
8. Haz clic en **Aceptar**  
9. Abre el símbolo del sistema y ejecuta: `mysql --version`  
10. Deberías ver la versión de MySQL instalada  

### 4. Instalación del Sistema de Nómina

#### La carpeta del programa incluye:
- Archivo Jar ejecutable del programa
- Archivo SQL inicial para la creación de la base de datos
- Guía de instalación

#### Para instalarlo:
1. [Haz clic aquí para ir a la carpeta](https://drive.google.com/drive/folders/1zgpcAIfiJos2C3Cu_xVxWDltm658KQ9s?usp=sharing)  
2. Da clic en **Descargar todo**
3. Elige la ubicación para guardar el archivo .zip, por ejemplo en el Escritorio
4. Da clic derecho sobre el archivo y selecciona **Extraer todo**
5. Navega hasta la carpeta **Sistema de Nomina OCM**
6. Dentro de la carpeta da clic derecho y selecciona **Abrir en terminal**
7. Si se abre con PowerShell escribe: `cmd`
8. Para importar la base de datos ejecuta el comando: `mysql -u root -p < .\import_inicial.sql`
9. Escribe la contraseña que configuraste durante la instalación de MySQL
10. Ahora se puede abrir el ejecutable del programa dándole doble clic o con el comando: `java -jar .\sistema-nomina-ocm.jar`

#### Usuarios del sistema:
- Para iniciar sesión como administrador utiliza el usuario **admin** y la contraseña **admin123**
- Para iniciar sesión como capturista utiliza el usuario **capturista** y la contraseña **capturista123**

### Capturas
![Login](screenshots/login.png)

