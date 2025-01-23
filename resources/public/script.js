document.getElementById("benchmark-form").onsubmit = async function(e) {
  e.preventDefault();
  const formData = new FormData(e.target);
  const response = await fetch("/benchmark", {
    method: "POST",
    body: new URLSearchParams(formData),
  });
  const result = await response.text();
  const match = result.match(/\{:result \[(.*?)\], :execution-time ([\d.]+)\}/);

  if (match) {
    const result = match[1]; // Extract the value inside [:result ...]
    const executionTime = match[2]; // Extract the :execution-time value

    console.log(`position: [${result}]`);
    console.log(`execution time: ${executionTime}`);
    document.getElementById("result").innerText = `Result: [${result}] \n Execution Time: ${executionTime}`;
  } else {
    console.error("Failed to parse the input string.");
      document.getElementById("result").innerText = `Failed to parse the input string.`;

  }
};


