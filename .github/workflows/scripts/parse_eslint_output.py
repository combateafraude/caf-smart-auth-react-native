import json

def parse_eslint_output():
    try:
        with open("eslint_output.json", "r") as f:
          output = f.read()
    except:
        print("The file 'eslint_output.json' could not be found.")
        exit(1)

    # Load JSON data
    parsed_data = json.loads(output)

    # Count errors and warnings
    errors = sum(obj["errorCount"] for obj in parsed_data)
    warnings = sum(obj["warningCount"] for obj in parsed_data)
    files = len(parsed_data)

    with open("eslint_summary.txt", "w") as summary:
        if errors == 0 and warnings == 0:
            summary.write(f"✅ Eslint Succeeded!\n\n")
        else:
            summary.write(f"🛑 Eslint Failed!\n\n")
            summary.write(f"⚠️ Warnings: {warnings}\n")
            summary.write(f"🚨 Errors: {errors}\n")
            summary.write(f"📂 Files validated: {files}\n")
 
    
if __name__ == "__main__":
    parse_eslint_output()
