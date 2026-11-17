(() => {
    const teachingSelect = document.getElementById("enseignementId");
    const enrollmentSelect = document.getElementById("inscriptionId");
    const helpText = document.getElementById("teacher-student-help");

    if (!teachingSelect || !enrollmentSelect || !helpText) {
        return;
    }

    const enrollmentOptions = Array.from(enrollmentSelect.options)
        .filter((option) => option.value !== "");

    function updateEnrollments(preserveSelection) {
        const selectedTeaching = teachingSelect.options[teachingSelect.selectedIndex];
        const promotionId = selectedTeaching ? selectedTeaching.dataset.promotionId || "" : "";
        const previousSelection = preserveSelection
            ? enrollmentSelect.dataset.selectedEnrollment || enrollmentSelect.value
            : "";
        let visibleCount = 0;
        let selectedOption = null;

        enrollmentOptions.forEach((option) => {
            const matches = promotionId !== "" && option.dataset.promotionId === promotionId;
            option.hidden = !matches;
            option.disabled = !matches;
            if (matches) {
                visibleCount += 1;
                if (option.value === previousSelection) {
                    selectedOption = option;
                }
            }
        });

        enrollmentSelect.value = selectedOption ? selectedOption.value : "";
        enrollmentSelect.disabled = visibleCount === 0;

        if (promotionId === "") {
            helpText.textContent = "Sélectionnez un enseignement pour afficher les étudiants inscrits.";
        } else if (visibleCount === 0) {
            helpText.textContent = "Aucun étudiant n'est inscrit à cette promotion.";
        } else {
            helpText.textContent = "";
        }

        enrollmentSelect.dataset.selectedEnrollment = "";
    }

    teachingSelect.addEventListener("change", () => updateEnrollments(false));
    updateEnrollments(true);
})();
