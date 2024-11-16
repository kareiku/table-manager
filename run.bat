@echo off
python --version
if %errorlevel% neq 0 (
    echo Python is not installed. Press any key to exit...
    pause
    exit /b
)

pip --version
if %errorlevel% neq 0 (
    python -m ensurepip
)

pip show tksheet
if %errorlevel% neq 0 (
    pip install tksheet
)

pip show pandas
if %errorlevel% neq 0 (
    pip install pandas
)

python ./main.py
