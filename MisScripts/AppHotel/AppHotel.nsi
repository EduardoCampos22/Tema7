
;--------------------------------
;Include Modern UI

    !include "MUI2.nsh"

;--------------------------------
;Variables
    var AppHostelJar
;--------------------------------
;General

    ;Name and file
    Name "Modern UI Test"
    OutFile "AppHostel.exe"
    Unicode True

    ;Default installation folder
    InstallDir "$LOCALAPPDATA\AppHostel"

    ;Get installation folder from registry if available
    InstallDirRegKey HKCU "Software\AppHostel" ""

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
    !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\AppHostel" 
    !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "App Hostel"
    
    !insertmacro MUI_PAGE_STARTMENU Application $AppHostelJar
    
    !insertmacro MUI_PAGE_INSTFILES
    
    !insertmacro MUI_UNPAGE_CONFIRM
    !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

    !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "App Hostel" SecDummy

    SetOutPath "$INSTDIR"

    ;ADD YOUR OWN FILES HERE...
    File "AppHotel.7z"
    Nsis7z::Extract "AppHotel.7z"
    Delete "AppHotel.7z"

    ;Store installation folder
    WriteRegStr HKCU "Software\AppsHotel" "" $INSTDIR

    ;Create uninstaller
    WriteUninstaller "$INSTDIR\UninstallAppHostel.exe"

    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
        ;Create shortcuts
        CreateDirectory "$SMPROGRAMS\$AppHostelJar"
        CreateShortcut "$SMPROGRAMS\$AppHostelJar\AppHostel.lnk" "$INSTDIR\sAppHostel.jar" 
        CreateShortcut "$SMPROGRAMS\$AppHostelJar\Uninstall.lnk" "$INSTDIR\UninstallAppHostel.exe"       
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

    !insertmacro MUI_STARTMENU_GETFOLDER Application $AppHostelJar
    
    ;Delete shortcuts
    Delete "$SMPROGRAMS\$AppHostelJar\AppHostel.lnk"
    Delete "$SMPROGRAMS\$AppHostelJar\Uninstall.lnk"
    RMDir "$SMPROGRAMS\$AppHostelJar"

    DeleteRegKey /ifempty HKCU "Software\AppHostel"

SectionEnd
