
;--------------------------------
;Include Modern UI

    !include "MUI2.nsh"

;--------------------------------
;Variables
    var LanzaAyudaJar
;--------------------------------
;General

    ;Name and file
    Name "Modern UI Test"
    OutFile "LanzaAyuda.exe"
    Unicode True

    ;Default installation folder
    InstallDir "$LOCALAPPDATA\LanzaAyuda"

    ;Get installation folder from registry if available
    InstallDirRegKey HKCU "Software\LanzaAyuda" ""

    ;Request application privileges for Windows Vista
    RequestExecutionLevel admin

;--------------------------------
;Interface Settings

    !define MUI_ABORTWARNING

;--------------------------------
;Pages

    !insertmacro MUI_PAGE_WELCOME
    !insertmacro MUI_PAGE_LICENSE "License.txt"
    !insertmacro MUI_PAGE_COMPONENTS
    !insertmacro MUI_PAGE_DIRECTORY


    ;Start Menu Folder Page Configuration
    !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
    !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\LanzaAyuda" 
    !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Lanza Ayuda"
    
    !insertmacro MUI_PAGE_STARTMENU Application $LanzaAyudaJar
    
    !insertmacro MUI_PAGE_INSTFILES
    
    !insertmacro MUI_UNPAGE_CONFIRM
    !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

    !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Lanza Ayuda" SecDummy

    SetOutPath "$INSTDIR"

    ;ADD YOUR OWN FILES HERE...
    File "LanzaAyuda.7z"
    Nsis7z::Extract "LanzaAyuda.7z"
    Delete "LanzaAyuda.7z"

    ;Store installation folder
    WriteRegStr HKCU "Software\LanzaAyuda" "" $INSTDIR

    ;Create uninstaller
    WriteUninstaller "$INSTDIR\UninstallLanzaAyuda.exe"

    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
        ;Create shortcuts
        CreateDirectory "$SMPROGRAMS\$LanzaAyudaJar"
        CreateShortcut "$SMPROGRAMS\$LanzaAyudaJar\LanzaAyuda.lnk" "$INSTDIR\LanzaAyuda.jar" 
        CreateShortcut "$SMPROGRAMS\$LanzaAyudaJar\Uninstall.lnk" "$INSTDIR\UninstallLanzaAyuda.exe"       
    !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;--------------------------------
;Descriptions

    ;Language strings
    LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

    ;Assign language strings to sections
    !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
    !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

    RMDir /r /REBOOTOK "$INSTDIR"

    !insertmacro MUI_STARTMENU_GETFOLDER Application $LanzaAyudaJar
    
    ;Delete shortcuts
    Delete "$SMPROGRAMS\$LanzaAyudaJar\LanzaAyuda.lnk"
    Delete "$SMPROGRAMS\$LanzaAyudaJar\Uninstall.lnk"
    RMDir "$SMPROGRAMS\$LanzaAyudaJar"

    DeleteRegKey /ifempty HKCU "Software\LanzaAyuda"

SectionEnd
