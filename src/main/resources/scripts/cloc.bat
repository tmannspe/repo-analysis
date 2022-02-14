cd %~dp0\..\git_Repos\%FILE_NAME%
%PERL% %CLOC% ./ --by-file --csv --quiet --report-file=%~dp0\..\logs\lines.csv
