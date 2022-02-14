cd %~dp0\..\git_Repos\%FILE_NAME%
git fetch
set log= %~dp0\..\logs\git.log
git log --pretty=format:"[%%h] %%an %%ad %%s" --date=short --numstat %FLAG_BEFORE%=%DATE_BEFORE% %FLAG_AFTER%=%DATE_AFTER%> %log%