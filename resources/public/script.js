document.getElementById("benchmark-form").onsubmit = async function(e) {
  e.preventDefault();
  const formData = new FormData(e.target);
  const response = await fetch("/benchmark", {
    method: "POST",
    body: new URLSearchParams(formData),
  });
  const result = await response.text();
  document.getElementById("result").innerText = result;
};
